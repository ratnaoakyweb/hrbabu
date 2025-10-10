package com.hrbabu.tracking.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrbabu.tracking.databinding.ItemLeaveBinding
import com.hrbabu.tracking.request_response.getLeave.LeavesItem
import com.hrbabu.tracking.utils.getFormattedDate

class LeaveAdapter(private val leaveList: List<LeavesItem?>) :
    RecyclerView.Adapter<LeaveAdapter.LeaveViewHolder>() {

    inner class LeaveViewHolder(private val binding: ItemLeaveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LeavesItem?) = with(binding) {
            tvEmployeeName.text = item?.employeeName ?: "N/A"
            tvLeaveType.text = item?.leaveType ?: "-"
            tvStatus.text = item?.status ?: "-"
            if(item?.status == "Approved"){
                tvStatus.setTextColor(root.context.getColor(android.R.color.holo_green_dark))
            } else if(item?.status == "Pending"){
                tvStatus.setTextColor(root.context.getColor(android.R.color.holo_orange_dark))
            } else if(item?.status == "Rejected"){
                tvStatus.setTextColor(root.context.getColor(android.R.color.holo_red_dark))
            } else {
                tvStatus.setTextColor(root.context.getColor(android.R.color.black))
            }
            tvReason.text = item?.reason ?: "-"
            val start = getFormattedDate(item?.startDate ?: "")
            val end = getFormattedDate(item?.endDate ?: "")

            tvDateRange.text = if (start == end || end.isEmpty()) {
                start
            } else {
                "$start to $end"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaveViewHolder {
        val binding = ItemLeaveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LeaveViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaveViewHolder, position: Int) {
        holder.bind(leaveList[position])
    }

    override fun getItemCount(): Int = leaveList.size
}