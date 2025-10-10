package com.hrbabu.tracking.request_response.applyLeave

import com.google.gson.annotations.SerializedName

data class SaveEmpLeaveRequest(

	@field:SerializedName("Flag")
	val flag: String? = null,

	@field:SerializedName("StartDate")
	val startDate: String? = null,

	@field:SerializedName("EndDate")
	val endDate: String? = null,

	@field:SerializedName("TotalDays")
	val totalDays: Int? = null,

	@field:SerializedName("LeaveTypeId")
	val leaveTypeId: Int? = null,


	@field:SerializedName("Reason")
	val reason: String? = null
)
