package com.hrbabu.tracking.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hrbabu.tracking.R
import com.hrbabu.tracking.request_response.empvisit.VisitsItem

class VisitAdapter (private val list: List<VisitsItem?>?) :
    RecyclerView.Adapter<VisitAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.date)
        val name: TextView = view.findViewById(R.id.name)
        val approvedBy: TextView = view.findViewById(R.id.approvedBy)
        val status: TextView = view.findViewById(R.id.status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visit, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = list!![position]!!
        holder.date.text = item.visitDate
        holder.name.text = item.clientName
//        holder.approvedBy.text = item.approvedBy
//        holder.status.text = item.status
//
//        holder.status.setTextColor(
//            when (item.status.lowercase()) {
//                "approved" -> 0xFF00C853.toInt()
//                "pending" -> 0xFFFFC107.toInt()
//                else -> 0xFF999999.toInt()
//            }
//        )
    }

    override fun getItemCount() = list!!.size
}