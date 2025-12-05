package com.hrbabu.tracking.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.hrbabu.tracking.BaseActivity
import com.hrbabu.tracking.R
import com.hrbabu.tracking.adapter.AttendanceListAdapter
import com.hrbabu.tracking.adapter.ClientAdapter
import com.hrbabu.tracking.databinding.ActivityClientListBinding
import com.hrbabu.tracking.databinding.ActivityViewAttendanceBinding
import com.hrbabu.tracking.helpers.ActivityAttendanceHistoryHelper
import com.hrbabu.tracking.helpers.ActivityAttendanceHistoryHelper.Companion.GET_ATTENDANCE_LIST
import com.hrbabu.tracking.helpers.ActivityClientListHelper
import com.hrbabu.tracking.helpers.ActivityClientListHelper.Companion.GET_CLIENT_LIST
import com.hrbabu.tracking.request_response.attendanceHistory.AttendanceItem
import com.hrbabu.tracking.request_response.getclient.ClientsItem
import java.util.Calendar

class ActivityAttendanceHistory : BaseActivity() {

    private lateinit var binding: ActivityViewAttendanceBinding
    private lateinit var adapter: AttendanceListAdapter
    var clientList: List<AttendanceItem?> = listOf()
    private  lateinit var activityAttendanceHistoryHelper : ActivityAttendanceHistoryHelper
    private var fromDate: String = ""
    private var toDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityAttendanceHistoryHelper = ActivityAttendanceHistoryHelper(this)
        activityAttendanceHistoryHelper.init(this)
        setupRecyclerView()

        activityAttendanceHistoryHelper.hitApi(GET_ATTENDANCE_LIST)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.etFromDate.setText(activityAttendanceHistoryHelper.getCurrentDate())
        binding.etToDate.setText(activityAttendanceHistoryHelper.getCurrentDate())
        fromDate= activityAttendanceHistoryHelper.getCurrentDate()
        toDate= activityAttendanceHistoryHelper.getCurrentDate()
        binding.etFromDate.setOnClickListener {
            showDatePicker(isFromDate = true)
        }

        binding.etToDate.setOnClickListener {
            showDatePicker(isFromDate = false)
        }



    }
    private fun showDatePicker(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            val selected = String.format("%04d-%02d-%02d", y, m + 1, d)

            if (isFromDate) {
                fromDate = selected
                binding.etFromDate.setText(selected)
            } else {
                toDate = selected
                binding.etToDate.setText(selected)
            }

            checkAndHitApi()

        }, year, month, day).show()
    }

    private fun checkAndHitApi() {
        if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
            if (fromDate.isNotEmpty() && toDate.isNotEmpty()) {
                if (toDate < fromDate) {
                    Toast.makeText(this, "To Date cannot be earlier than From Date", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            activityAttendanceHistoryHelper.hitApi(GET_ATTENDANCE_LIST, fromDate, toDate)
        }
    }



    private fun setupRecyclerView() {
        binding.rvList.layoutManager = LinearLayoutManager(this)
    }

    fun setUpRecyclerView(attendanceList: List<AttendanceItem?>? ) {

        if (!attendanceList.isNullOrEmpty()) {
            binding.emptyView.visibility = View.GONE
            binding.rvList.visibility = View.VISIBLE
            clientList = attendanceList
            adapter = AttendanceListAdapter(clientList,object : AttendanceListAdapter.OnClientClickListener {
                override fun onClientClick(client: AttendanceItem?) {
                    showHalfDayDialog()
//                    Toast.makeText(this@ActivityAttendanceHistory, "Selected: ${client?.clientId}", Toast.LENGTH_SHORT).show()
//                    //send to previus activity on result
//                    val intent = intent
//                    intent.putExtra("clientId", client?.clientId)
//                    setResult(RESULT_OK, intent)
//                    finish()
                // Handle client click event here
                }

            })
            binding.rvList.adapter = adapter

//            setupSearch()
        } else {

            binding.emptyView.visibility = View.VISIBLE
            binding.rvList.visibility = View.GONE

        }
    }
    private fun showHalfDayDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_halfday_cancel)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)

        val etReason = dialog.findViewById<EditText>(R.id.etReason)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)
        val btnCancel = dialog.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
            val reason = etReason.text.toString().trim()
            if (reason.isEmpty()) {
                Toast.makeText(this, "Please enter a reason", Toast.LENGTH_SHORT).show()
            } else {
                // send API
                //Toast.makeText(this, "Submitted: $reason", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                //hit api saveHalfDayRequest
                activityAttendanceHistoryHelper.saveHalfDayRequest(reason)
            }
        }

        dialog.show()
    }


    override fun onDestroy() {
        super.onDestroy()

    }
}