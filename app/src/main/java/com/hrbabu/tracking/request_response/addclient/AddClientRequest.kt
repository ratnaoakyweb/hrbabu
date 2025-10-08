package com.hrbabu.tracking.request_response.addclient

import com.google.gson.annotations.SerializedName

data class AddClientRequest(

	@field:SerializedName("ClientName")
	val clientName: String? = null,

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("CreatedBy")
	val createdBy: Int? = null,

	@field:SerializedName("CompanyId")
	val companyId: Int? = null,

	@field:SerializedName("Address")
	val address: String? = null,

	@field:SerializedName("Phone")
	val phone: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Boolean? = null,

	@field:SerializedName("ClientId")
	val clientId: Int? = null,

	@field:SerializedName("Website")
	val website: String? = null,

	@field:SerializedName("Flag")
	val flag: String? = "I",

	@field:SerializedName("LocationLat")
	val locationLat: String? = null,

	@field:SerializedName("LocationLong")
	val locationLong: String? = null
)
