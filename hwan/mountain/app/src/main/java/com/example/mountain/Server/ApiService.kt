package com.example.mountain.Server

import com.example.mountain.DataModel.DataResponse
import com.example.mountain.DataModel.SignUpDataRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // 사용자 추가
    @POST("/api/user")
    fun createUser(@Body userData: SignUpDataRequest): Call<DataResponse>

    // 사용자 조회 (ID로 조회)
    @GET("/api/user/{id}")
    fun getUserById(@Path("id") id: Int): Call<DataResponse>

    // 사용자 목록 조회
    @GET("/api/users")
    fun getAllUsers(): Call<List<DataResponse>>
}
