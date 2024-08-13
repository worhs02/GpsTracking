package com.example.mountain.Server

import com.example.mountain.DataModel.UserDataResponse
import com.example.mountain.DataModel.SignUpDataRequest
import com.example.mountain.DataModel.RoomDataRequest
import com.example.mountain.DataModel.RoomDataResponse
import com.example.mountain.DataModel.UserProfileResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // 사용자 추가

    // 사용자 조회 (ID로 조회)
    @GET("/api/user/{id}")
    fun getUserById(@Path("id") id: Int): Call<UserDataResponse>

    // 사용자 목록 조회
    @GET("/api/users")
    fun getAllUsers(): Call<List<UserDataResponse>>

    @GET("checkTagNum")
    fun checkTagNumExists(@Query("tagNum") tagNum: Int): Call<UserDataResponse>

    @POST("createUser")
    fun createUser(@Body userData: SignUpDataRequest): Call<UserDataResponse>

    @PUT("users/{id}/tag")
    fun updateTag(@Path("id") userId: Int, @Query("tagNum") tagNum: Int): Call<DataResponse>
  
    @GET("/getUserProfileByEmail")
    suspend fun getUserProfileByEmail(@Query("email") email: String): UserProfileResponse

  
    // 방 관련 API

    // 방 생성
    @POST("/api/rooms")
    fun createRoom(@Body roomData: RoomDataRequest): Call<RoomDataResponse>

    // 모든 방 조회
    @GET("/api/rooms")
    fun getAllRooms(): Call<List<RoomDataResponse>>
}
