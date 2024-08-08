package com.example.mountain.DataModel

data class DataResponse(
    val id: Int,
    val username: String,
    val email: String,
    val passwordHash: String
)