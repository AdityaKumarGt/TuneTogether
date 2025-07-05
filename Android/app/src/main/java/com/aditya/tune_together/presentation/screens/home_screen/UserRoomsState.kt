package com.aditya.tune_together.presentation.screens.home_screen

import com.aditya.tune_together.domain.model.sign_in.User
import com.aditya.tune_together.domain.model.user_rooms.UserRoom

data class UserRoomsState(
    val isLoading: Boolean = false,
    val userRooms: List<UserRoom>? = null,
    val error: String = "",

    )