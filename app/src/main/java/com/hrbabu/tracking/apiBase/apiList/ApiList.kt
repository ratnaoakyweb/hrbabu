package com.hrbabu.tracking.apiBase.apiList

import io.reactivex.Observable
import com.hrbabu.tracking.request_response.getResponse.GetResponse
import com.hrbabu.tracking.request_response.getclient.GetClientRequest
import com.hrbabu.tracking.request_response.getclient.GetClientResponse
import com.hrbabu.tracking.request_response.history.HistoryResponse
import com.hrbabu.tracking.request_response.login.LoginRequest
import com.hrbabu.tracking.request_response.login.LoginResponse
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


    @POST("/api/Employee/GetClients")
    fun getClients(@Body request: GetClientRequest): Observable<GetClientResponse>

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
//        @Part("VisitId") VisitId: RequestBody,
        @Part("CheckInTime") CheckInTime: RequestBody?,
        @Part("CheckInLat") CheckInLat: RequestBody,
        @Part("CheckInLng") CheckInLng: RequestBody?,
//        @Part("CheckInPhotoUrl") CheckInPhotoUrl: RequestBody,
        @Part("CheckOutTime") CheckOutTime: RequestBody?,
        @Part("CheckOutLat") CheckOutLat: RequestBody?,
        @Part("CheckOutLng") CheckOutLng: RequestBody?,
//        @Part("CheckOutPhotoUrl") CheckOutPhotoUrl: RequestBody?,
    ): Observable<PunchinPunchoutResponse>

}