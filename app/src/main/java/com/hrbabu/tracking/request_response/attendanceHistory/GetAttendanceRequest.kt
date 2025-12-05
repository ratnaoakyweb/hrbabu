package com.hrbabu.tracking.request_response.attendanceHistory

import com.google.gson.annotations.SerializedName

data class GetAttendanceRequest(

	@field:SerializedName("PageSize")
	val pageSize: Int? = null,

	@field:SerializedName("PageNumber")
	val pageNumber: Int? = null,

	@field:SerializedName("IsActive")
	val isActive: Boolean? = null,

	@field:SerializedName("Search")
	val search: String? = null,

	@field:SerializedName("ShiftId")
	val shiftId: Any? = null,

	@field:SerializedName("FromDate")
	val fromDate: String? = null,

	@field:SerializedName("ToDate")
	val toDate: String? = null
)
