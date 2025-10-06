package com.hrbabu.tracking.request_response.history

import com.google.gson.annotations.SerializedName

data class HistoryResponse(

	@field:SerializedName("rs")
	val rs: Int? = null,

	@field:SerializedName("res")
	val res: Res? = null,

	@field:SerializedName("rc")
	val rc: List<RcItem?>? = null,

	@field:SerializedName("msgkey")
	val msgkey: String? = null
)

data class RcItem(

	@field:SerializedName("ClientName")
	val clientName: Any? = null,

	@field:SerializedName("ActivityType")
	val activityType: String? = null,

	@field:SerializedName("EmployeeId")
	val employeeId: Int? = null,

	@field:SerializedName("ActivityTime")
	val activityTime: String? = null,

	@field:SerializedName("VisitId")
	val visitId: Any? = null
)

data class Res(

	@field:SerializedName("ClientName")
	val clientName: Any? = null,

	@field:SerializedName("ActivityType")
	val activityType: Any? = null,

	@field:SerializedName("EmployeeId")
	val employeeId: Int? = null,

	@field:SerializedName("ActivityTime")
	val activityTime: Any? = null,

	@field:SerializedName("VisitId")
	val visitId: Any? = null
)
