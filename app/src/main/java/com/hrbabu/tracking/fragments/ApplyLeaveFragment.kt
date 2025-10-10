package com.hrbabu.tracking.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hrbabu.tracking.activity.ApplyLeaveActivity
import com.hrbabu.tracking.databinding.FragmentApplyLeaveBinding
import com.hrbabu.tracking.helpers.ApplyLeaveHelper
import java.text.SimpleDateFormat
import java.util.*

class ApplyLeaveFragment : Fragment() {

    private var _binding: FragmentApplyLeaveBinding? = null
    private val binding get() = _binding!!

    private var fromDate: Calendar? = null
    private var toDate: Calendar? = null
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplyLeaveBinding.inflate(inflater, container, false)

        setupDatePickers()

        binding.incHeader.ivClose.setOnClickListener {
            activity?.finish()
        }

        binding.btnApply.setOnClickListener {
            validateAndApplyLeave()
        }

        return binding.root
    }

    fun setUpData(res: List<com.hrbabu.tracking.request_response.alldropdown.RcItem?>?) {
        val leaveTypes = res?.mapNotNull { it?.text } ?: listOf("Leave")
        val leaveId = res?.mapNotNull { it?.value } ?: listOf(0)
        (activity as ApplyLeaveActivity).leaveId = leaveId.firstOrNull() ?: 0
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, leaveTypes)
        binding.spinnerLeaveType.adapter = adapter

        binding.spinnerLeaveType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (activity as ApplyLeaveActivity).leaveId = leaveId[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optional: handle case when nothing is selected
            }
        }
    }

    private fun validateAndApplyLeave() {
        val reason = binding.etReason.text.toString().trim()

        when {
            fromDate == null -> {
                showToast("Please select From Date")
                return
            }
            toDate == null -> {
                showToast("Please select To Date")
                return
            }
            reason.isEmpty() -> {
                showToast("Please enter reason for leave")
                return
            }
        }

        // If all validations pass
        (activity as ApplyLeaveActivity).reason = reason
        (activity as ApplyLeaveActivity).helper.hitApi(ApplyLeaveHelper.SAVE_EMP_LEAVE)
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }




    private fun setupDatePickers() {
        binding.tvFromDate.setOnClickListener {
            openDatePicker(isFromDate = true)
        }

        binding.tvToDate.setOnClickListener {
            openDatePicker(isFromDate = false)
        }
    }

    private fun openDatePicker(isFromDate: Boolean) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedCal = Calendar.getInstance().apply {
                    set(selectedYear, selectedMonth, selectedDay)
                }

                if (isFromDate) {
                    // Convert to UTC for server
                    val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val utcDateString = utcFormat.format(selectedCal.time)
                    (activity as ApplyLeaveActivity).startDate = utcDateString

                    fromDate = selectedCal
                    binding.tvFromDate.text = dateFormat.format(selectedCal.time)
                    // Reset toDate if invalid
                    if (toDate != null && toDate!!.before(fromDate)) {
                        toDate = null
                        binding.tvToDate.text = "Select Date"
                    }
                } else {
                    // Convert to UTC for server
                    val utcFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    utcFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val utcDateString = utcFormat.format(selectedCal.time)

                    toDate = selectedCal
                    binding.tvToDate.text = dateFormat.format(selectedCal.time)
                    (activity as ApplyLeaveActivity).endDate = utcDateString
                }

                updateLeaveCount()
            },
            year, month, day
        )

        // Disable past dates
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000

        // If selecting "To Date", make sure minDate is after From Date
        if (!isFromDate && fromDate != null) {
            datePicker.datePicker.minDate = fromDate!!.timeInMillis
        }

        datePicker.show()
    }

    private fun updateLeaveCount() {
        if (fromDate != null && toDate != null) {
            val diff = toDate!!.timeInMillis - fromDate!!.timeInMillis
            val days = (diff / (1000 * 60 * 60 * 24)) + 1
            binding.tvLeaveCount.text = "$days Day${if (days > 1) "s" else ""}"
            (activity as ApplyLeaveActivity).totalDays = days.toInt()
        } else {
            binding.tvLeaveCount.text = "0 Days"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
