package com.hrbabu.tracking.activity


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hrbabu.tracking.databinding.ActivityAddVisitBinding
import com.hrbabu.tracking.helpers.ActivityAddVisitHelper
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AddVisitActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddVisitBinding
    private val calendar = Calendar.getInstance()
    private lateinit var helper: ActivityAddVisitHelper
    val clientNames = mutableListOf("Select Client")
    val clientId = mutableListOf("Select Client")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVisitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        helper = ActivityAddVisitHelper(this)
        helper.init(this)
        helper.hitApi(ActivityAddVisitHelper.GET_CLIENT_LIST)
    }

    fun setUpClients(clients: List<com.hrbabu.tracking.request_response.getclient.ClientsItem?>?) {

        clients?.forEach {
            clientNames.add(it?.clientName ?: "Unknown")
            clientId.add(it?.clientId.toString())
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, clientNames)
        binding.spinnerClient.adapter = adapter
    }

    private fun setupUI() {
        // Back button
        binding.btnBack.setOnClickListener { finish() }

//        // Clients - dummy data
//        val clients = listOf("Select Client", "Client A", "Client B", "Client C")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, clients)
//        binding.spinnerClient.adapter = adapter

        // Pre-fill visit date
//        binding.etVisitDate.setText(getCurrentUtcTime())

        // Pick date
        binding.etVisitDate.setOnClickListener { openDatePicker() }

        // Pick From time
        binding.etFromTime.setOnClickListener { openTimePicker(binding.etFromTime) }

        // Pick To time
        binding.etToTime.setOnClickListener { openTimePicker(binding.etToTime) }

        // Save click
        binding.btnSaveVisit.setOnClickListener { saveVisit() }
    }

    private fun openDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            val selectedDate = String.format("%04d-%02d-%02d", y, m + 1, d)
            binding.etVisitDate.setText("${selectedDate}")
        }, year, month, day).show()
    }

    private fun openTimePicker(target: android.widget.EditText) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, h, m ->
            val timeStr = String.format("%02d:%02d:00.000Z", h, m)
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            target.setText("${todayDate}T$timeStr")
        }, hour, minute, true).show()
    }

    private fun saveVisit() {
        val visitDate = binding.etVisitDate.text.toString().trim()
        val clientPos = binding.spinnerClient.selectedItemPosition
        val purpose = binding.etPurpose.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val fromTime = binding.etFromTime.text.toString().trim()
        val toTime = binding.etToTime.text.toString().trim()

        if (clientPos == 0) {
            showToast("Select a client")
            return
        }
        if (purpose.isEmpty()) {
            showToast("Enter purpose")
            return
        }
        if (description.isEmpty()) {
            showToast("Enter description")
            return
        }

        val json = JSONObject().apply {
            put("Flag", "I")
            put("VisitDate", visitDate)
            put("ClientId", clientPos) // Replace with real clientId
            put("Purpose", purpose)
            put("Description", description)
            put("FromTime", fromTime)
            put("ToTime", toTime)
        }
        clientId.get(clientPos)
        helper.hitApi(ActivityAddVisitHelper.ADD_VISIT)


    }

//    private fun getCurrentUtcTime(): String {
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        sdf.timeZone = TimeZone.getTimeZone("UTC")
//        return sdf.format(Date())
//    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
