package com.hrbabu.tracking.request_response.empvisit

import com.google.gson.annotations.SerializedName

data class EmpVisitResponse(

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

	@field:SerializedName("Visits")
	val visits: List<VisitsItem?>? = null,

	@field:SerializedName("TotalRecords")
	val totalRecords: Int? = null
)

data class VisitsItem(

	@field:SerializedName("Description")
	val description: String? = null,

	@field:SerializedName("CreatedBy")
	val createdBy: Int? = null,

	@field:SerializedName("CompanyId")
	val companyId: Int? = null,

	@field:SerializedName("IsActive")
	val isActive: Boolean? = null,

	@field:SerializedName("Purpose")
	val purpose: String? = null,

	@field:SerializedName("VisitId")
	val visitId: Int? = null,

	@field:SerializedName("VisitDate")
	val visitDate: String? = null,

	@field:SerializedName("ClientName")
	val clientName: String? = null,

	@field:SerializedName("ToTime")
	val toTime: String? = null,

	@field:SerializedName("IsPhoto")
	val isPhoto: Boolean? = null,

	@field:SerializedName("CreatedDate")
	val createdDate: String? = null,

	@field:SerializedName("ClientId")
	val clientId: Int? = null,

	@field:SerializedName("EmployeeId")
	val employeeId: Int? = null,

	@field:SerializedName("FromTime")
	val fromTime: String? = null
)
