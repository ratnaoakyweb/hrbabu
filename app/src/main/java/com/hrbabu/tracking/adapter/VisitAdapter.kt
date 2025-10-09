package com.hrbabu.tracking.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hrbabu.tracking.R
import com.hrbabu.tracking.request_response.empvisit.VisitsItem
import com.hrbabu.tracking.utils.getFormattedTime

class VisitAdapter (private val list: List<VisitsItem?>? ,
    private  val onItemClick: ((VisitsItem?) -> Unit)
    ) :
    RecyclerView.Adapter<VisitAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClientName: TextView = view.findViewById(R.id.tvClientName)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val tvStartTime: TextView = view.findViewById(R.id.tvStartTime)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_visit, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val item = list!![position]!!

        holder.tvClientName.text = item.clientName
        holder.tvStatus.text = item.StatusText.toString()
        when(item.status){
            1 -> {
                holder.tvStatus.setTextColor(0xFF34A853.toInt())
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed)
            }// Green for Completed
            2 ->{
                holder.tvStatus.setTextColor(0xFF34A853.toInt())
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed)
            } // Amber for On Going
            else -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                holder.tvStatus.setTextColor(0xFFF4B400.toInt())
            }// Grey for Pending
        }
        holder.tvStatus.text = when(item.status){
            1 -> "Completed"
            2 -> "On Going"
            else -> "Pending"
        }

        holder.tvAddress.text = item.address

        holder.tvStartTime.text = getFormattedTime(item.fromTime ?: "")
        holder.tvDescription.text = item.description

        holder.itemView.setOnClickListener {
            if(item.status!=1){
            onItemClick(item)
            }
        }
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