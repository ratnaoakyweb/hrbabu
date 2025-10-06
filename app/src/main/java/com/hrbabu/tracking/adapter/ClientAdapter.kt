package com.hrbabu.tracking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrbabu.tracking.databinding.ItemClientBinding
import com.hrbabu.tracking.request_response.getclient.ClientsItem

class ClientAdapter(private var clients: List<ClientsItem?>,  private val listener: OnClientClickListener) :
    RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {
    interface OnClientClickListener {
        fun onClientClick(client: ClientsItem?)
    }

    inner class ClientViewHolder(val binding: ItemClientBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ItemClientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        holder.binding.tvClientName.text = client?.clientName
        holder.binding.tvClientAddress.text = client?.address
        holder.binding.root.setOnClickListener {
            listener.onClientClick(client)
        }
    }

    override fun getItemCount(): Int = clients.size

    fun filterList(filteredList: List<ClientsItem?>) {
        clients = filteredList
        notifyDataSetChanged()
    }
}