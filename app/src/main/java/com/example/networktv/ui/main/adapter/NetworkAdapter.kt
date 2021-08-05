package com.example.networktv.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.networktv.data.model.TV
import com.example.networktv.databinding.NetworkTvItemBinding

class NetworkAdapter(private val tvList: ArrayList<TV>) : RecyclerView.Adapter<NetworkAdapter.DataViewHolder>() {

    private lateinit var binding: NetworkTvItemBinding

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = NetworkTvItemBinding.inflate(layoutInflater)
        val view = binding.root
        return DataViewHolder(
            view
        )
    }

    override fun getItemCount(): Int = tvList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val tv = tvList[position]
        binding.tvName.text = tv.name
        binding.tvIP.text = tv.ip
    }

    fun addData(list: List<TV>) {
        tvList.addAll(list)
    }
}