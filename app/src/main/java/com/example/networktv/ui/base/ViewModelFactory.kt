package com.example.networktv.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.networktv.data.repository.TVNetworkRepository
import com.example.networktv.data.service.NSDHelper
import com.example.networktv.ui.main.viewmodel.NetworkViewModel

class ViewModelFactory(private val nsdHelper: NSDHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NetworkViewModel::class.java)) {
            return NetworkViewModel(TVNetworkRepository(nsdHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}