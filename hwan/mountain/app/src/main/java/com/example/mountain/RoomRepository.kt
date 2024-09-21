package com.example.mountain

import com.example.mountain.DataModel.RoomDataResponse

object RoomRepository {
    private val rooms = mutableListOf<RoomDataResponse>()
    private var nextRoomId = 1

    fun addRoom(room: RoomDataResponse) {
        rooms.add(room)
    }

    fun getRooms(): List<RoomDataResponse> {
        return rooms
    }

    fun getNextRoomId(): Int {
        return nextRoomId++
    }
}
