package com.aditya.tune_together.domain.model.user_rooms

data class UserRoom(
    val hostEmail: String,
    val hostName: String,
    val isCreatedByYou: Boolean,
    val members: List<Member>,
    val roomId: String,
    val roomName: String,
    val startTime: String
)