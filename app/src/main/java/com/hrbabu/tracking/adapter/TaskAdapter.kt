package com.hrbabu.tracking.adapter

//TaskAdapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hrbabu.tracking.R
import com.hrbabu.tracking.databinding.ItemTaskBinding
import com.hrbabu.tracking.request_response.history.RcItem
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private val taskList: List<RcItem>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.binding.tvTitle.text = task.activityType ?: "No Activity"
        holder.binding.tvTime.text = formatTime(task.activityTime)
        holder.binding.tvDate.text = formatDate(task.activityTime)
        holder.binding.tvStatus.text = if (task.activityType != null) "Completed" else "Pending"

        // Change icon based on ActivityType
        holder.binding.icIcon.setImageResource(
            when (task.activityType) {
                "Punch In" -> R.drawable.icon_checkin
                "Punch Out" -> R.drawable.icon_checkout
                "Visit Check In" -> R.drawable.icon_checkin
                "Visit Check Out" -> R.drawable.icon_checkout
                else -> R.drawable.icon_checkout
            }
        )
    }

    override fun getItemCount(): Int = taskList.size

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            formatter.timeZone = TimeZone.getDefault()
            val date = parser.parse(dateString)
            formatter.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

    private fun formatTime(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone("UTC")
            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            formatter.timeZone = TimeZone.getDefault()
            val date = parser.parse(dateString)
            formatter.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
}