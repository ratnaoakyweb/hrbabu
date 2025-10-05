package com.hrbabu.tracking.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hrbabu.tracking.databinding.FragmentApplyLeaveBinding
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

        setupLeaveTypeDropdown()
        setupDatePickers()

        binding.incHeader.ivClose.setOnClickListener {
            activity?.finish()
        }

        binding.btnApply.setOnClickListener {
            validateAndApplyLeave()
        }

        return binding.root
    }

    private fun validateAndApplyLeave() {
        val reason = binding.etReason.text.toString().trim()

        when {
            fromDate == null -> {
                showToast("Please select From Date")
            }
            toDate == null -> {
                showToast("Please select To Date")
            }
            reason.isEmpty() -> {
                showToast("Please enter reason for leave")
            }
            else -> {
                showToast("Leave Applied Successfully!")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    private fun setupLeaveTypeDropdown() {
        val leaveTypes = listOf("Casual Leave", "Sick Leave", "Earned Leave")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, leaveTypes)
        binding.spinnerLeaveType.setAdapter(adapter)
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
                    fromDate = selectedCal
                    binding.tvFromDate.text = dateFormat.format(selectedCal.time)
                    // Reset toDate if invalid
                    if (toDate != null && toDate!!.before(fromDate)) {
                        toDate = null
                        binding.tvToDate.text = "Select Date"
                    }
                } else {
                    toDate = selectedCal
                    binding.tvToDate.text = dateFormat.format(selectedCal.time)
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
        } else {
            binding.tvLeaveCount.text = "0 Days"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
