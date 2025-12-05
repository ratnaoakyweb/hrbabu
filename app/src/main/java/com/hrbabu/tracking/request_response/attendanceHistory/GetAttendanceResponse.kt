package com.hrbabu.tracking.request_response.attendanceHistory

import com.google.gson.annotations.SerializedName

data class GetAttendanceResponse(

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

	@field:SerializedName("Attendance")
	val attendance: List<AttendanceItem?>? = null,

	@field:SerializedName("TotalRecords")
	val totalRecords: Int? = null
)

data class AttendanceItem(

	@field:SerializedName("CheckInLat")
	val checkInLat: Double? = null,

	@field:SerializedName("CheckInLng")
	val checkInLng: Double? = null,

	@field:SerializedName("ShiftName")
	val shiftName: String? = null,

	@field:SerializedName("EmployeeCode")
	val employeeCode: String? = null,

	@field:SerializedName("TotalWorkingHours")
	val totalWorkingHours: String? = null,

	@field:SerializedName("AttendanceDate")
	val attendanceDate: String? = null,

	@field:SerializedName("LastCheckOut")
	val lastCheckOut: String? = null,

	@field:SerializedName("IsHalfDayApproved")
	val isHalfDayApproved: Boolean? = null,

	@field:SerializedName("CheckOutLng")
	val checkOutLng: Double? = null,

	@field:SerializedName("CheckOutPhotoUrl")
	val checkOutPhotoUrl: String? = null,

	@field:SerializedName("CheckInPhotoUrl")
	val checkInPhotoUrl: String? = null,

	@field:SerializedName("CheckOutLat")
	val checkOutLat: Double? = null,

	@field:SerializedName("IsHalfDay")
	val isHalfDay: Boolean? = null,

	@field:SerializedName("LocationName")
	val locationName: String? = null,

	@field:SerializedName("FirstCheckIn")
	val firstCheckIn: String? = null,

	@field:SerializedName("AttendanceStatus")
	val attendanceStatus: String? = null,

	@field:SerializedName("DesignationName")
	val designationName: String? = null,

	@field:SerializedName("OvertimeMinutes")
	val overtimeMinutes: Int? = null,

	@field:SerializedName("DepartmentName")
	val departmentName: String? = null,

	@field:SerializedName("LateMinutes")
	val lateMinutes: Int? = null,

	@field:SerializedName("EmployeeId")
	val employeeId: Int? = null,

	@field:SerializedName("EmployeeName")
	val employeeName: String? = null
)
