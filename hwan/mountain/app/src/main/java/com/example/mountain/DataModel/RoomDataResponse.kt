package com.example.mountain.DataModel

data class RoomDataResponse(
    val id: Int,
    val roomName: String,
    val password: String?,
    val participantCount: Int,
    val maxParticipants: Int  // 최대 인원 수 필드 추가
)
