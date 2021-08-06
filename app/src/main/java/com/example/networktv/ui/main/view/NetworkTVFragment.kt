package com.example.networktv.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networktv.data.model.TV
import com.example.networktv.data.service.NSDEvent
import com.example.networktv.data.service.NSDHelper
import com.example.networktv.data.service.NSDServiceImpl
import com.example.networktv.data.service.NSDState
import com.example.networktv.databinding.LoaderElementBinding
import com.example.networktv.databinding.NetworkTvLayoutBinding
import com.example.networktv.ui.base.ViewModelFactory
import com.example.networktv.ui.main.adapter.NetworkAdapter
import com.example.networktv.ui.main.viewmodel.NetworkViewModel
import com.example.networktv.utils.Logger
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NetworkTVFragment : BottomSheetDialogFragment() {
    private val className = this.javaClass.simpleName

    private lateinit var viewModel: NetworkViewModel
    private lateinit var adapter: NetworkAdapter

    private lateinit var networkLayoutBinding: NetworkTvLayoutBinding
    private lateinit var loaderElementBinding: LoaderElementBinding

    private lateinit var nsdServiceImpl: NSDServiceImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        networkLayoutBinding = NetworkTvLayoutBinding.inflate(layoutInflater)
        loaderElementBinding = networkLayoutBinding.loader
        return networkLayoutBinding.root
    }

    override fun onResume() {
        super.onResume()

        Logger.debug("$className, tvList: ${ viewModel.getState().value?.tvList }")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewModel()
        initObserver()

        nsdServiceImpl.startListening()

        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {
        networkLayoutBinding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = NetworkAdapter(arrayListOf())
        networkLayoutBinding.recyclerView.adapter = adapter
    }

    private fun initObserver() {
        viewModel.getState().observe(this) {
            Logger.info("$className, event: ${ it.event }")
            when (it.event) {
                NSDEvent.SERVICE_EMPTY -> {
                    Logger.debug("$className, show something on service empty event")
                }
                NSDEvent.SERVICE_SEARCHING,
                NSDEvent.SERVICE_RESOLVED,
                NSDEvent.SERVICE_LOST -> {
                    it.tvList?.let { it1 -> checkTVList(it1, it) }
                }
                NSDEvent.SERVICE_SEARCHING_FAILED -> {
                    Logger.debug("$className, show something on searching failed event")
                }
            }
        }
    }

    private fun checkTVList(tvList: List<TV>, nsdState: NSDState) {
        if (tvList.isEmpty()) {
            showLoader()
        } else {
            nsdState.tvList?.let { tv -> renderList(tv) }
            showRecycler()
        }
    }

    private fun showLoader() {
        loaderElementBinding.progressBarLoad.visibility = View.VISIBLE
        networkLayoutBinding.recyclerView.visibility = View.GONE
    }

    private fun showRecycler() {
        loaderElementBinding.progressBarLoad.visibility = View.GONE
        networkLayoutBinding.recyclerView.visibility = View.VISIBLE
    }

    private fun renderList(tvList: List<TV>) {
        adapter.addData(tvList)
        adapter.notifyDataSetChanged()
    }

    private fun initViewModel() {
        nsdServiceImpl = NSDServiceImpl(context)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(NSDHelper(nsdServiceImpl))
        ).get(NetworkViewModel::class.java)

        Logger.debug("$className, initViewModel: $viewModel")
    }

}