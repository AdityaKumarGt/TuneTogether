package com.aditya.tune_together.data.remote.dto_userRooms

data class UserRoom(
    val _id: String,
    val hostEmail: String,
    val hostName: String,
    val isCreatedByYou: Boolean,
    val members: List<Member>,
    val roomId: String,
    val roomName: String,
    val startTime: String
)