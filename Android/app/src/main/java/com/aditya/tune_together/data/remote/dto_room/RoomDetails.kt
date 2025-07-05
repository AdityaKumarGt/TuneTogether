package com.aditya.tune_together.data.remote.dto_room

data class RoomDetails(
    val _id: String,
    val createdAt: String,
    val hostEmail: String,
    val hostFcmToken: String,
    val hostId: String,
    val hostName: String,
    val members: List<Member>,
    val roomName: String,
    val songs: List<Song>,
    val startTime: String,
    val updatedAt: String
)