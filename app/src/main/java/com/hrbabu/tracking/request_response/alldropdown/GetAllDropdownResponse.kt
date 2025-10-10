package com.hrbabu.tracking.request_response.alldropdown

import com.google.gson.annotations.SerializedName

data class GetAllDropdownResponse(

	@field:SerializedName("rs")
	val rs: Int? = null,

	@field:SerializedName("res")
	val res: Res? = null,

	@field:SerializedName("rc")
	val rc: List<RcItem?>? = null,

	@field:SerializedName("msgkey")
	val msgkey: Any? = null
)

data class RcItem(

	@field:SerializedName("Value")
	val value: Int? = null,

	@field:SerializedName("Text")
	val text: String? = null
)

data class Res(

	@field:SerializedName("Value")
	val value: Any? = null,

	@field:SerializedName("Text")
	val text: Any? = null
)
