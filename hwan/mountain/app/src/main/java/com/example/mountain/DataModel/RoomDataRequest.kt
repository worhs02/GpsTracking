package com.example.mountain.DataModel

data class RoomDataRequest(
    val roomName: String,
    val password: String?,
    val maxParticipants: Int  // 최대 인원 수 필드 추가
)
