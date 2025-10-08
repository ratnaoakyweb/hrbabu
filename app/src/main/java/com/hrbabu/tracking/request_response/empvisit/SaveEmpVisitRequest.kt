package com.hrbabu.tracking.request_response.empvisit

import com.google.gson.annotations.SerializedName

data class SaveEmpVisitRequest(

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("ToTime")
	val toTime: String? = null,

	@field:SerializedName("IsPhoto")
	val isPhoto: Boolean? = null,

	@field:SerializedName("IsActive")
	val isActive: Boolean? = null,

	@field:SerializedName("ClientId")
	val clientId: Int? = null,

	@field:SerializedName("Purpose")
	val purpose: String? = null,

	@field:SerializedName("VisitDate")
	val visitDate: String? = null,

	@field:SerializedName("Flag")
	val flag: String? = null,

	@field:SerializedName("FromTime")
	val fromTime: String? = null
)
