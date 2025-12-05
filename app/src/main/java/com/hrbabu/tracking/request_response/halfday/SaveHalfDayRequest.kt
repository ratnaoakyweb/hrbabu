package com.hrbabu.tracking.request_response.halfday

import com.google.gson.annotations.SerializedName

data class SaveHalfDayRequest(

	@field:SerializedName("Reason")
	val reason: String? = null
)
