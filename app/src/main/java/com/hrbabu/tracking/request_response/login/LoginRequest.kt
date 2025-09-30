package com.hrbabu.tracking.request_response.login

import com.google.gson.annotations.SerializedName

data class LoginRequest(

	@field:SerializedName("Login")
    var login: String? = null,

	@field:SerializedName("Password")
	var password: String? = null
)
