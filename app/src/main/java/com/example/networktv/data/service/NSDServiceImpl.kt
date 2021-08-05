package com.example.networktv.data.service

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.DiscoveryListener
import android.net.nsd.NsdServiceInfo
import android.os.Build
import com.example.networktv.data.model.TV
import com.example.networktv.utils.Constants
import com.example.networktv.utils.Constants.Companion.ATTRIBUTE_HWID
import com.example.networktv.utils.Constants.Companion.DEFAULT_PORT
import com.example.networktv.utils.Constants.Companion.NSD_SERVICE_TYPE
import com.example.networktv.utils.Constants.Companion.OHD_HTTP_PORT
import com.example.networktv.utils.Logger
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean

class NSDServiceImpl(private val context: Context?) : NSDService {

    private lateinit var nsdManager: NsdManager

    private val state = PublishSubject.create<NSDState>()

    private var discoveryListener: DiscoveryListener? = null

    private val resolveListenerBusy = AtomicBoolean(false)
    private val pendingNsdServices = ConcurrentLinkedQueue<NsdServiceInfo>()
    private val resolvedNsdServices = Collections.synchronizedList(ArrayList<TV>())

    private val className = this.javaClass.simpleName

    override fun getState(): Observable<NSDState> {
        return state
    }

    fun startListening() {
        try {
            if (isListening()) {
                stopListening()
            }
            initializeDiscoveryListener()
            nsdManager = context?.getSystemService(Context.NSD_SERVICE) as NsdManager
            nsdManager.discoverServices(
                NSD_SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD,
                discoveryListener
            )
            setState(NSDEvent.SERVICE_INIT)
        } catch (e: Exception) {
            Logger.error("$className, startListening exception: $e")
            e.printStackTrace()
        }
    }

    private fun stopListening() {
        if (discoveryListener != null) {
            try {
                nsdManager.stopServiceDiscovery(discoveryListener)
            } catch (e: Exception) {
                Logger.error("$className, stopListening exception: $e")
                e.printStackTrace()
            }
            discoveryListener = null
        }
    }

    private fun isListening(): Boolean {
        return discoveryListener != null
    }

    private fun startResolveService(serviceInfo: NsdServiceInfo) {
        val newResolveListener: NsdManager.ResolveListener = object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Logger.error("$className, test resolve failed with errorCode: $errorCode")
                setState(NSDEvent.SERVICE_SEARCHING_FAILED)
                resolveNextInQueue()
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                val serviceName = serviceInfo.serviceName
                val ip = serviceInfo.host.hostAddress
                val tv = TV(serviceName, ip)
                if (serviceInfo.port == DEFAULT_PORT || serviceInfo.port == OHD_HTTP_PORT) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        serviceInfo.attributes[ATTRIBUTE_HWID]?.let {
                            tv.hwid = String(
                                it,
                                StandardCharsets.UTF_8
                            )
                        }
                    }
                    resolvedNsdServices.add(tv)
                    setState(NSDEvent.SERVICE_RESOLVED)
                    Logger.verbose("$className, send name: ${ tv.name }, ip: ${ tv.ip }, hwid: ${ tv.hwid }")
                }
                resolveNextInQueue()
            }
        }
        nsdManager.resolveService(serviceInfo, newResolveListener)
    }

    private fun initializeDiscoveryListener() {
        discoveryListener = object : DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Logger.error("$className, test discovery failed errorCode: $errorCode, serviceType: $serviceType")
                setState(NSDEvent.SERVICE_SEARCHING_FAILED)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Logger.error("$className, test stopping discovery failed errorCode: $errorCode")
                setState(NSDEvent.SERVICE_SEARCHING_FAILED)
            }

            override fun onDiscoveryStarted(serviceType: String) {
                setState(NSDEvent.SERVICE_SEARCHING)
                Logger.verbose("$className, test discovery started")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Logger.warning("$className, test discovery stopped")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                processServiceFound(serviceInfo)
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                processServiceLost(serviceInfo)
            }
        }
    }

    private fun processServiceFound(serviceInfo: NsdServiceInfo) {
        Logger.verbose("$className, test service found, service name: ${ serviceInfo.serviceName }")

        if (serviceInfo.serviceType != NSD_SERVICE_TYPE) {
            Logger.error("$className, unknown service type: ${ serviceInfo.serviceName }")
        } else if (serviceInfo.serviceName == Constants.NSD_SERVICE_NAME) {
            Logger.warning("$className, same machine: ${ serviceInfo.serviceName }")
        } else {
            if (resolveListenerBusy.compareAndSet(false, true)) {
                startResolveService(serviceInfo)
            } else {
                pendingNsdServices.add(serviceInfo)
            }
        }
    }

    private fun processServiceLost(serviceInfo: NsdServiceInfo) {
        Logger.error("$className, test service lost: ${ serviceInfo.serviceName }")
        try {
            val iterator = pendingNsdServices.iterator()
            while (iterator.hasNext()) {
                if (iterator.next().serviceName == serviceInfo.serviceName) iterator.remove()
            }
            synchronized(resolvedNsdServices) {
                val iterator = resolvedNsdServices.iterator()
                while (iterator.hasNext()) {
                    if (iterator.next()
                            .name == serviceInfo.serviceName
                    ) iterator.remove()
                }
            }
            if (resolvedNsdServices.isEmpty()) {
                setState(NSDEvent.SERVICE_EMPTY)
            }
            setState(NSDEvent.SERVICE_LOST)
        } catch (e: Exception) {
            Logger.error("NSDClient, test service lost exception: $e")
            e.printStackTrace()
        }
    }

    private fun resolveNextInQueue() {
        val nextNsdService = pendingNsdServices.poll()
        if (nextNsdService != null) {
            startResolveService(nextNsdService)
        } else {
            resolveListenerBusy.set(false)
        }
    }

    private fun setState(nsdEvent: NSDEvent) {
        val nsdState = NSDState(resolvedNsdServices, nsdEvent)
        state.onNext(nsdState)
    }
}