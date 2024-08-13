package com.example.mountain.DataModel

data class UserDataResponse(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String,
    val tag: Int,
)