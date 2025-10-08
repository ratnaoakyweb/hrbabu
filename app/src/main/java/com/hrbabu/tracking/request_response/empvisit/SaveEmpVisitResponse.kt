package com.hrbabu.tracking.request_response.empvisit

import com.google.gson.annotations.SerializedName

data class SaveEmpVisitResponse(

	@field:SerializedName("rs")
	val rs: Int? = null,

	@field:SerializedName("res")
	val res: Res1? = null,

	@field:SerializedName("rc")
	val rc: List<Any?>? = null,

	@field:SerializedName("msgkey")
	val msgkey: String? = null
)

data class Res1(

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("Id")
	val id: Any? = null,

	@field:SerializedName("StatusCode")
	val statusCode: Int? = null
)
