package com.example.mountain.DataModel

data class UserProfileResponse(
    val nickname: String,
    val tags: List<String> // 태그가 문자열 리스트라고 가정
)
