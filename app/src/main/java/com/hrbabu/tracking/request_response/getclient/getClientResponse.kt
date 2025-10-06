package com.hrbabu.tracking.request_response.getclient

import com.google.gson.annotations.SerializedName

data class ClientsItem(

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

	@field:SerializedName("CreatedDate")
	val createdDate: String? = null,

	@field:SerializedName("ClientId")
	val clientId: Int? = null,

	@field:SerializedName("Website")
	val website: String? = null,

	@field:SerializedName("LocationLat")
	val locationLat: String? = null,

	@field:SerializedName("LocationLong")
	val locationLong: String? = null
)

data class Res(

	@field:SerializedName("TotalRecords")
	val totalRecords: Int? = null,

	@field:SerializedName("Clients")
	val clients: List<ClientsItem?>? = null
)

data class GetClientResponse(

	@field:SerializedName("rs")
	val rs: Int? = null,

	@field:SerializedName("res")
	val res: Res? = null,

	@field:SerializedName("rc")
	val rc: List<Any?>? = null,

	@field:SerializedName("msgkey")
	val msgkey: String? = null
)
