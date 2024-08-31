package com.example.mountain

import com.naver.maps.geometry.LatLng
import java.io.Serializable

data class ExerciseRecord(
    val distance: Float, // 거리 (킬로미터)
    val calories: Float, // 칼로리 소모량
    val time: String, // 운동 시간
    val path: List<LatLng> // 경로 좌표 리스트
) : Serializable
