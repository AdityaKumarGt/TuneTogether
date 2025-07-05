package com.aditya.tune_together.data.remote.dto_register_user

data class RegisterUserRequest(
    val uid: String,
    val name: String,
    val email: String,
    val profilePhoto: String,
    val fcmToken: String

)
