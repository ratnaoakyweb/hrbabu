package com.hrbabu.tracking.request_response.getclient

import com.google.gson.annotations.SerializedName

data class GetClientRequest(

	@field:SerializedName("PageSize")
	val pageSize: Int? = 1000,

	@field:SerializedName("PageNumber")
	val pageNumber: Int? = 1,

	@field:SerializedName("IsActive")
	val isActive: Boolean? = true,

	@field:SerializedName("Search")
	val search: String? = ""
)
