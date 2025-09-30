package com.hrbabu.tracking.apiBase.apiList

import io.reactivex.Observable
import com.hrbabu.tracking.request_response.getResponse.GetResponse
import com.hrbabu.tracking.request_response.login.LoginRequest
import com.hrbabu.tracking.request_response.login.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiList {

    @POST("/api/Employee/login")
    fun empLogin(@Body request: LoginRequest): Observable<LoginResponse>

    @GET("/api/auth/GetResponseMessage")
    fun getResponse(): Observable<GetResponse>

}