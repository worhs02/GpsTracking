package com.example.mountain.Server

import com.example.mountain.DataModel.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("/api/auth/register")
    fun createUser(@Body userData: SignUpDataRequest): Call<UserDataResponse>

    @PUT("/api/users/{id}/tag")
    fun updateTag(@Path("id") userId: Int, @Body tagNum: Int): Call<UserDataResponse>

    @GET("/api/users")
    fun getAllUsers(): Call<List<UserDataResponse>>

    @GET("/api/user/profile")
    suspend fun getUserProfileByEmail(@Query("email") email: String): UserProfileResponse

    @POST("/api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/calendarCreate")
    fun sendSelectedDate(@Body dateRequest: CalendarRequest): Call<Void>
}
