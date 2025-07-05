package com.aditya.tune_together.presentation.screens.room

import com.aditya.tune_together.domain.model.room.Room
import com.aditya.tune_together.domain.model.user_rooms.UserRoom

data class RoomState(
    val isLoading: Boolean = false,
    val room: Room? = null,
    val error: String = "",

    )