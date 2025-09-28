package com.hrbabu.tracking.request_response.login

import com.google.gson.annotations.SerializedName
import com.hrbabu.tracking.request_response.base.BaseResponse

data class LoginResponse(

	@field:SerializedName("res")
	val res: Res? = null,

	@field:SerializedName("rc")
	val rc: List<Any?>? = null,

) : BaseResponse()

data class Res(

	@field:SerializedName("DesignationId")
	val designationId: Int? = null,

	@field:SerializedName("AccountId")
	val accountId: Int? = null,

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("EmployeeCode")
	val employeeCode: String? = null,

	@field:SerializedName("CompanyId")
	val companyId: Int? = null,

	@field:SerializedName("Message")
	val message: String? = null,

	@field:SerializedName("LocationId")
	val locationId: Int? = null,

	@field:SerializedName("ShiftId")
	val shiftId: Int? = null,

	@field:SerializedName("IsLocked")
	val isLocked: Boolean? = null,

	@field:SerializedName("StatusCode")
	val statusCode: Int? = null,

	@field:SerializedName("JwtToken")
	val jwtToken: String? = null,

	@field:SerializedName("Username")
	val username: String? = null,

	@field:SerializedName("FullName")
	val fullName: Any? = null,

	@field:SerializedName("LastLoginAt")
	val lastLoginAt: String? = null,

	@field:SerializedName("IsEmailConfirmed")
	val isEmailConfirmed: Boolean? = null,

	@field:SerializedName("DepartmentId")
	val departmentId: Int? = null,

	@field:SerializedName("EmployeeId")
	val employeeId: Int? = null,

	@field:SerializedName("ManagerEmployeeId")
	val managerEmployeeId: Int? = null
)
