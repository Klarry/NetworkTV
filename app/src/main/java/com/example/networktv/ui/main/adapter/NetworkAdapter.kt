package com.example.networktv.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.networktv.R
import com.example.networktv.data.model.TV
import com.example.networktv.utils.Logger

class NetworkAdapter(private val tvList: ArrayList<TV>) : RecyclerView.Adapter<NetworkAdapter.DataViewHolder>() {
    private val className = this.javaClass.simpleName

    inner class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName : TextView = itemView.findViewById(R.id.tvName) as TextView
        var tvIp : TextView = itemView.findViewById(R.id.tvIP) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.network_tv_item, parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = tvList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        val tv = tvList[position]
        holder.tvIp.text = tv.ip
        holder.tvName.text = tv.name

        Logger.info("$className, name: ${ holder.tvName.text }, ip: ${  holder.tvIp.text }")
    }

    fun addData(list: List<TV>) {
        tvList.addAll(list)
    }
}