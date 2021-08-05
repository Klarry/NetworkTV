package com.example.networktv.ui.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networktv.data.service.NSDEvent
import com.example.networktv.data.service.NSDHelper
import com.example.networktv.data.service.NSDServiceImpl
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

    private lateinit var binding: NetworkTvLayoutBinding

    private lateinit var nsdServiceImpl: NSDServiceImpl

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = NetworkTvLayoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupViewModel()
        setupObserver()

        nsdServiceImpl.startListening()
    }

    private fun setupUI() {
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = NetworkAdapter(arrayListOf())
        binding.recyclerView.adapter = adapter
    }

    private fun setupObserver() {
        viewModel.getState().observe(this) {
            Logger.info("$className, event: " + it.event)
            when (it.event) {
                NSDEvent.SERVICE_INIT -> {}
                NSDEvent.SERVICE_LOST -> {}
                NSDEvent.SERVICE_EMPTY -> {}
                NSDEvent.SERVICE_SEARCHING -> {}
                NSDEvent.SERVICE_SEARCHING_FAILED -> {}
                NSDEvent.SERVICE_RESOLVED -> {}
            }
        }
    }

    private fun setupViewModel() {
        nsdServiceImpl = NSDServiceImpl(context)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(NSDHelper(nsdServiceImpl))
        ).get(NetworkViewModel::class.java)
    }

}