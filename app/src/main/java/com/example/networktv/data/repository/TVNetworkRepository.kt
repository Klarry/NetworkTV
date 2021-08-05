package com.example.networktv.data.repository

import com.example.networktv.data.service.NSDHelper
import com.example.networktv.data.service.NSDState
import io.reactivex.Observable

class TVNetworkRepository(private val nsdHelper: NSDHelper) {
    fun getState(): Observable<NSDState> {
        return nsdHelper.getState()
    }
}