package com.hrbabu.tracking.request_response.punchinpunchout

import com.google.gson.annotations.SerializedName

data class PunchinPunchoutResponse(

	@field:SerializedName("rs")
	val rs: Int? = null,

	@field:SerializedName("res")
	val res: Res? = null,

	@field:SerializedName("rc")
	val rc: List<Any?>? = null,

	@field:SerializedName("msgkey")
	val msgkey: String? = null
)

data class Res(

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("Id")
	val id: Any? = null,

	@field:SerializedName("StatusCode")
	val statusCode: Int? = null,

	@field:SerializedName("VisitCheckInId")
	val VisitCheckInId: Int? = null,

	@field:SerializedName("ActivityTime")
	val activityTime: String? = null
)
