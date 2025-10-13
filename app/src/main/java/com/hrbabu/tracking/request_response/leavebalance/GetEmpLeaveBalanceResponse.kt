package com.hrbabu.tracking.request_response.leavebalance

import com.google.gson.annotations.SerializedName

data class GetEmpLeaveBalanceResponse(

	@field:SerializedName("rs")
	val rs: Int? = null,

	@field:SerializedName("res")
	val res: Res? = null,

	@field:SerializedName("rc")
	val rc: List<Any?>? = null,

	@field:SerializedName("msgkey")
	val msgkey: String? = null
)

data class LeaveBalancesItem(

	@field:SerializedName("NoOfHours")
	val noOfHours: Int? = null,

	@field:SerializedName("LeaveTypeName")
	val leaveTypeName: String? = null,

	@field:SerializedName("NoOfLeave")
	val noOfLeave: Int? = null,

	@field:SerializedName("LeaveTypeId")
	val leaveTypeId: Int? = null,

	@field:SerializedName("NoOfLeavesAnnualy")
	val noOfLeavesAnnualy: Int? = null
)

data class Res(

	@field:SerializedName("LateComingCount")
	val lateComingCount: Int? = null,

	@field:SerializedName("TotalOvertimeHours")
	val totalOvertimeHours: Int? = null,

	@field:SerializedName("LeaveBalances")
	val leaveBalances: List<LeaveBalancesItem?>? = null,

	@field:SerializedName("AvgWorkingHours")
	val avgWorkingHours: Any? = null
)
