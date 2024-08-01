package com.example.mountain.Server

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://semper16paratus06.iptime.org:3306" // 서버 주소
    private const val USERNAME = "song" // 실제 사용자 이름으로 교체
    private const val PASSWORD = "1q2w3e4r@S" // 실제 비밀번호로 교체

    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val original: Request = chain.request()
            val credentials = Credentials.basic(USERNAME, PASSWORD)
            val request: Request = original.newBuilder()
                .header("Authorization", credentials)
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        })
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
