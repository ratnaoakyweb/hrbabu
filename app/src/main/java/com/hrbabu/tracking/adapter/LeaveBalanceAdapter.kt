package com.hrbabu.tracking.adapter

import android.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrbabu.tracking.databinding.ItemLeaveBalanceBinding
import com.hrbabu.tracking.databinding.ItemLeaveBinding
import com.hrbabu.tracking.request_response.getLeave.LeavesItem
import com.hrbabu.tracking.request_response.leavebalance.LeaveBalancesItem
import com.hrbabu.tracking.utils.getFormattedDate

class LeaveBalanceAdapter(private val leaveList: List<LeaveBalancesItem?>) :
    RecyclerView.Adapter<LeaveBalanceAdapter.LeaveViewHolder>() {

    inner class LeaveViewHolder(private val binding: ItemLeaveBalanceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeaveBalancesItem?) = with(binding) {

            binding.tvLeaveTypeName.text = item?.leaveTypeName ?: ""
            binding.tvNoOfLeaves.text = item?.noOfLeavesAnnualy?.toString() ?: "0"
            val noOfleaves =(item?.noOfLeavesAnnualy ?: 0) - (item?.noOfLeave ?: 0)

            binding.tvLeaveConsumed.text = when (noOfleaves) {
                0 -> "0 day consumed"
                1 -> "1 day consumed"
                else -> "$noOfleaves days consumed"
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val binding = ItemLeaveBalanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        holder.bind(leaveList[position])
    }

    override fun getItemCount(): Int = leaveList.size
}