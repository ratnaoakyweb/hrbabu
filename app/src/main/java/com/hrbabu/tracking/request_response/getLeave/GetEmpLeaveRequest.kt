package com.hrbabu.tracking.request_response.getLeave

import com.google.gson.annotations.SerializedName

data class GetEmpLeaveRequest(

	@field:SerializedName("PageSize")
	val pageSize: Int? = null,

	@field:SerializedName("PageNumber")
	val pageNumber: Int? = null,

	@field:SerializedName("Search")
	val search: String? = null
)
