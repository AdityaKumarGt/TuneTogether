package com.aditya.tune_together.domain.model.room



data class Room (
    val hostEmail: String,
    val hostFcmToken: String,
    val hostId: String,
    val hostName: String,
    val members: List<Member>,
    val roomName: String,
    val songs: List<Song>,
    val startTime: String,
)