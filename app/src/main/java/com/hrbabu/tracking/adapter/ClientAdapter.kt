package com.hrbabu.tracking.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
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
        holder.binding.llPhone.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${client?.phone}")
            holder.binding.llPhone.context.startActivity(intent)
        }

        holder.binding.llNavigate.setOnClickListener {
            val latitude = client?.locationLat
            val longitude = client?.locationLong

            if (latitude != null && longitude != null) {

                val navigationIntentUri =
                    Uri.parse("google.navigation:q=" + latitude + "," + longitude) //creating intent with latlng
                val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                holder.binding.root.context.startActivity(mapIntent)

//                val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(client.clientName)})")
//                val intent = Intent(
//                    Intent.ACTION_VIEW,
//                    Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345")
//                )
//                holder.binding.root.context.startActivity(intent)



//                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//                mapIntent.setPackage("com.google.android.apps.maps")
//
//                val context = holder.binding.root.context
//                if (mapIntent.resolveActivity(context.packageManager) != null) {
//                    context.startActivity(mapIntent)
//                }
            } else {
                Toast.makeText(holder.binding.root.context, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = clients.size

    fun filterList(filteredList: List<ClientsItem?>) {
        clients = filteredList
        notifyDataSetChanged()
    }
}