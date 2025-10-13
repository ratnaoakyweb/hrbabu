package com.hrbabu.tracking.apiBase.apiList

import com.hrbabu.tracking.request_response.addclient.AddClientRequest
import com.hrbabu.tracking.request_response.alldropdown.GetAllDropdownResponse
import com.hrbabu.tracking.request_response.applyLeave.SaveEmpLeaveRequest
import com.hrbabu.tracking.request_response.applyLeave.SaveEmpLeaveResponse
import com.hrbabu.tracking.request_response.emptoggel.ResponseGetEmployeeActivityToggle
import com.hrbabu.tracking.request_response.empvisit.EmpVisitRequest
import com.hrbabu.tracking.request_response.empvisit.EmpVisitResponse
import com.hrbabu.tracking.request_response.empvisit.SaveEmpVisitRequest
import com.hrbabu.tracking.request_response.empvisit.SaveEmpVisitResponse
import com.hrbabu.tracking.request_response.getLeave.GetEmpLeaveRequest
import com.hrbabu.tracking.request_response.getLeave.GetLeaveResponse
import io.reactivex.Observable
import com.hrbabu.tracking.request_response.getResponse.GetResponse
import com.hrbabu.tracking.request_response.getclient.GetClientRequest
import com.hrbabu.tracking.request_response.getclient.GetClientResponse
import com.hrbabu.tracking.request_response.history.HistoryResponse
import com.hrbabu.tracking.request_response.leavebalance.GetEmpLeaveBalanceResponse
import com.hrbabu.tracking.request_response.login.LoginRequest
import com.hrbabu.tracking.request_response.login.LoginResponse
import com.hrbabu.tracking.request_response.profile.ProfileResponse
import com.hrbabu.tracking.request_response.punchinpunchout.PunchinPunchoutResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiList {

    @POST("/api/Employee/login")
    fun empLogin(@Body request: LoginRequest): Observable<LoginResponse>

    @GET("/api/auth/GetResponseMessage")
    fun getResponse(): Observable<GetResponse>


    @GET("/api/Employee/GetEmployeePunchingHistory")
    fun getHistory(): Observable<HistoryResponse>

    @GET("/api/Employee/GetEmployeeActivityToggleV2")
    fun getEmployeeActivityToggle(): Observable<ResponseGetEmployeeActivityToggle>

    @GET("/api/Employee/GetEmployeeProfile")
    fun getEmployeeProfile(): Observable<ProfileResponse>

    @POST("/api/Employee/GetEmployeeVisits")
    fun getEmployeeVisits(@Body request: EmpVisitRequest): Observable<EmpVisitResponse>

    @POST("/api/Employee/SaveEmployeeVisit")
    fun saveEmployeeVisit(@Body request: SaveEmpVisitRequest): Observable<SaveEmpVisitResponse>

    @POST("/api/Employee/GetClients")
    fun getClients(@Body request: GetClientRequest): Observable<GetClientResponse>

    @POST("/api/Employee/SaveClient")
    fun saveClients(@Body request: AddClientRequest): Observable<GetClientResponse>

    @Multipart
    @POST("api/Employee/EmpPunchInOut")
    fun empPunchInOut(
        @Part CheckInFile: MultipartBody.Part?,
        @Part CheckOutFile: MultipartBody.Part?,
        @Part("Flag") Flag: RequestBody,
        @Part("DeviceType") DeviceType: RequestBody,
        @Part("CheckInLat") CheckInLat: RequestBody,
        @Part("CheckOutTime") CheckOutTime: RequestBody?,
        @Part("CheckInLng") CheckInLng: RequestBody,
        @Part("CheckOutLat") CheckOutLat: RequestBody?,
        @Part("CheckInTime") CheckInTime: RequestBody,
        @Part("CheckOutLng") CheckOutLng: RequestBody?
    ): Observable<PunchinPunchoutResponse>


    @Multipart
    @POST("api/Employee/EmpCheckInOut")
    fun empCheckInCheckOut(
        @Part CheckInFile: MultipartBody.Part?,
        @Part CheckOutFile: MultipartBody.Part?,
        @Part("Flag") Flag: RequestBody,
        @Part("ClientId") ClientId: RequestBody,
        @Part("VisitId") VisitId: RequestBody,
        @Part("CheckInTime") CheckInTime: RequestBody?,
        @Part("CheckInLat") CheckInLat: RequestBody,
        @Part("CheckInLng") CheckInLng: RequestBody?,
//        @Part("CheckInPhotoUrl") CheckInPhotoUrl: RequestBody,
        @Part("CheckOutTime") CheckOutTime: RequestBody?,
        @Part("CheckOutLat") CheckOutLat: RequestBody?,
        @Part("CheckOutLng") CheckOutLng: RequestBody?,
        @Part("VisitCheckInId") VisitCheckInId: RequestBody?,
//        @Part("CheckOutPhotoUrl") CheckOutPhotoUrl: RequestBody?,
    ): Observable<PunchinPunchoutResponse>




    @GET("/api/Home/GetAllDropDown?Flag=LType")
    fun getAllDropDown(): Observable<GetAllDropdownResponse>

    @POST("/api/Employee/SaveEmployeeLeave")
    fun saveEmployeeLeave(@Body request : SaveEmpLeaveRequest): Observable<SaveEmpLeaveResponse>

    @POST("/api/Employee/GetEmployeeLeaves")
    fun getEmployeeLeaves(@Body request : GetEmpLeaveRequest): Observable<GetLeaveResponse>

    @GET("/api/Employee/GetEmployeeDashboard")
    fun getEmployeeLeavesBalance(): Observable<GetEmpLeaveBalanceResponse>

}