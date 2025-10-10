package com.hrbabu.tracking.request_response.getLeave

import com.google.gson.annotations.SerializedName

data class GetLeaveResponse(

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

	@field:SerializedName("Leaves")
	val leaves: List<LeavesItem?>? = null,

	@field:SerializedName("TotalRecords")
	val totalRecords: Int? = null
)

data class LeavesItem(

	@field:SerializedName("Status")
	val status: String? = null,

	@field:SerializedName("CompanyId")
	val companyId: Int? = null,

	@field:SerializedName("CancelledBy")
	val cancelledBy: Any? = null,

	@field:SerializedName("LeaveType")
	val leaveType: String? = null,

	@field:SerializedName("CreatedAt")
	val createdAt: String? = null,

	@field:SerializedName("CancelledAt")
	val cancelledAt: Any? = null,

	@field:SerializedName("LeaveTypeId")
	val leaveTypeId: Int? = null,

	@field:SerializedName("EndDate")
	val endDate: String? = null,

	@field:SerializedName("UpdatedAt")
	val updatedAt: Any? = null,

	@field:SerializedName("Reason")
	val reason: String? = null,

	@field:SerializedName("StartDate")
	val startDate: String? = null,

	@field:SerializedName("ApprovedAt")
	val approvedAt: Any? = null,

	@field:SerializedName("LeaveId")
	val leaveId: Int? = null,

	@field:SerializedName("ApprovedBy")
	val approvedBy: Any? = null,

	@field:SerializedName("TotalDays")
	val totalDays: Int? = null,

	@field:SerializedName("EmployeeId")
	val employeeId: Int? = null,

	@field:SerializedName("EmployeeName")
	val employeeName: String? = null
)
