package com.hrbabu.tracking.request_response.profile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(

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

	@field:SerializedName("Email")
	val email: String? = null,

	@field:SerializedName("AccountId")
	val accountId: Int? = null,

	@field:SerializedName("ManagerName")
	val managerName: String? = null,

	@field:SerializedName("ShiftCode")
	val shiftCode: String? = null,

	@field:SerializedName("IsActive")
	val isActive: Boolean? = null,

	@field:SerializedName("CreatedAt")
	val createdAt: String? = null,

	@field:SerializedName("ShiftId")
	val shiftId: Int? = null,

	@field:SerializedName("LocationName")
	val locationName: String? = null,

	@field:SerializedName("CompanyName")
	val companyName: String? = null,

	@field:SerializedName("JoiningDate")
	val joiningDate: String? = null,

	@field:SerializedName("Phone")
	val phone: String? = null,

	@field:SerializedName("DesignationName")
	val designationName: String? = null,

	@field:SerializedName("IsEmailConfirmed")
	val isEmailConfirmed: Boolean? = null,

	@field:SerializedName("EmployeeId")
	val employeeId: Int? = null,

	@field:SerializedName("DateOfBirth")
	val dateOfBirth: String? = null,

	@field:SerializedName("DesignationId")
	val designationId: Int? = null,

	@field:SerializedName("ShiftName")
	val shiftName: String? = null,

	@field:SerializedName("EmployeeCode")
	val employeeCode: String? = null,

	@field:SerializedName("CompanyId")
	val companyId: Int? = null,

	@field:SerializedName("LocationId")
	val locationId: Int? = null,

	@field:SerializedName("IsLocked")
	val isLocked: Boolean? = null,

	@field:SerializedName("UpdatedAt")
	val updatedAt: Any? = null,

	@field:SerializedName("BiometricId")
	val biometricId: String? = null,

	@field:SerializedName("Username")
	val username: String? = null,

	@field:SerializedName("FullName")
	val fullName: String? = null,

	@field:SerializedName("DepartmentName")
	val departmentName: String? = null,

	@field:SerializedName("LastLoginAt")
	val lastLoginAt: String? = null,

	@field:SerializedName("DepartmentId")
	val departmentId: Int? = null,

	@field:SerializedName("ManagerEmployeeId")
	val managerEmployeeId: Int? = null
)
