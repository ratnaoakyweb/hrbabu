package com.hrbabu.tracking.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hrbabu.tracking.databinding.ItemAttendanceEmpBinding
import com.hrbabu.tracking.databinding.ItemClientBinding
import com.hrbabu.tracking.request_response.attendanceHistory.AttendanceItem
import com.hrbabu.tracking.request_response.getclient.ClientsItem
import java.text.SimpleDateFormat
import java.util.Locale

class AttendanceListAdapter(private var attendanceList: List<AttendanceItem?>, private val listener: OnClientClickListener) :

    RecyclerView.Adapter<AttendanceListAdapter.ItemAttendanceViewHolder>() {
    interface OnClientClickListener {
        fun onClientClick(client: AttendanceItem?)
    }

    inner class ItemAttendanceViewHolder(val binding: ItemAttendanceEmpBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAttendanceViewHolder {
        val binding = ItemAttendanceEmpBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemAttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemAttendanceViewHolder, position: Int) {
    val attendanceRow = attendanceList[position]

    holder.binding.apply {
        tvEmpName.text = attendanceRow?.employeeName ?: "N/A"
        tvEmpDetails.text = buildString {
            attendanceRow?.employeeCode?.let { append("Emp Code: $it") }
            attendanceRow?.attendanceDate?.let {
                if (isNotEmpty()) append("   •   ")
                append(formatDate(it))
            }
            if (isEmpty()) append("N/A")
        }

        // Times and working hours
        tvCheckIn.text = formatTime(attendanceRow?.firstCheckIn) ?: "Not checked in"
        tvCheckOut.text = formatTime(attendanceRow?.lastCheckOut) ?: "Not checked out"
        tvWorkingHours.text = attendanceRow?.totalWorkingHours ?: "N/A"

        // Late badge
        val lateMinutes = attendanceRow?.lateMinutes ?: 0
        tvLate.visibility = if (lateMinutes > 0) android.view.View.VISIBLE else android.view.View.GONE
        tvLate.text = if (lateMinutes > 0) "Late by $lateMinutes min" else ""

        // Status / shift / plant
        tvStatus.text = attendanceRow?.attendanceStatus ?: "N/A"
        tvShift.text = attendanceRow?.shiftName ?: "N/A"
        tvPlant.text = attendanceRow?.locationName ?: "N/A"

        // Clicks: cancel / root -> forward to listener
        btnCancel.setOnClickListener { listener.onClientClick(attendanceRow) }
        root.setOnClickListener { listener.onClientClick(attendanceRow) }
        if(attendanceRow?.firstCheckIn ==null){
            tvViewLocation.visibility= android.view.View.GONE
            tvViewPhoto.visibility= android.view.View.GONE

        }

        if(attendanceRow?.lastCheckOut ==null){
            tvCheckOutLocation.visibility= android.view.View.GONE
            tvCheckOutPhoto.visibility= android.view.View.GONE

        }
        // View check-in location
        tvViewLocation.setOnClickListener {
            val lat = attendanceRow?.checkInLat
            val lon = attendanceRow?.checkInLng
            if (lat != null && lon != null) {
                val uri = Uri.parse("google.navigation:q=$lat,$lon")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                root.context.startActivity(mapIntent)
            } else {
                Toast.makeText(root.context, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }

        // View check-in photo
        tvViewPhoto.setOnClickListener {
            val url = attendanceRow?.checkInPhotoUrl
            if (!url.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                root.context.startActivity(intent)
            } else {
                Toast.makeText(root.context, "Photo not available", Toast.LENGTH_SHORT).show()
            }
        }

        // View check-out location
        tvCheckOutLocation.setOnClickListener {
            val lat = attendanceRow?.checkOutLat
            val lon = attendanceRow?.checkOutLng
            if (lat != null && lon != null) {
                val uri = Uri.parse("google.navigation:q=$lat,$lon")
                val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                    setPackage("com.google.android.apps.maps")
                }
                root.context.startActivity(mapIntent)
            } else {
                Toast.makeText(root.context, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }

        // View check-out photo
        tvCheckOutPhoto.setOnClickListener {
            val url = attendanceRow?.checkOutPhotoUrl
            if (!url.isNullOrBlank()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                root.context.startActivity(intent)
            } else {
                Toast.makeText(root.context, "Photo not available", Toast.LENGTH_SHORT).show()
            }
        }

        //btnCancel
        btnCancel.setOnClickListener { listener.onClientClick(attendanceRow)}
        if(attendanceRow?.isHalfDay == true && (attendanceRow.isHalfDayApproved == null || !attendanceRow.isHalfDayApproved)){
            btnCancel.visibility = android.view.View.VISIBLE
        }else{
            btnCancel.visibility = android.view.View.GONE
        }

        btnCancel.setOnClickListener {
            listener.onClientClick(attendanceRow)
        }
    }
}
    override fun getItemCount(): Int = attendanceList.size
    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return ""
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = parser.parse(dateString)
            formatter.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }
    private fun formatTime(dateString: String?): String? {
        if (dateString.isNullOrEmpty()) return null
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = parser.parse(dateString)
            formatter.format(date!!)
        } catch (e: Exception) {
            null
        }
    }

}