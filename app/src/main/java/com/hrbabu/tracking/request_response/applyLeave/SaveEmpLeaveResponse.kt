package com.hrbabu.tracking.request_response.applyLeave

import com.google.gson.annotations.SerializedName

data class SaveEmpLeaveResponse(

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
	val statusCode: Int? = null
)
