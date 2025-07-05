package com.aditya.tune_together.data.remote.dto_room

data class Member(
    val _id: String,
    val name: String,
    val email: String,
    val fcmToken: String
)