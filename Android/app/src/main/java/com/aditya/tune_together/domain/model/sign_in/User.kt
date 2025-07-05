package com.aditya.tune_together.domain.model.sign_in

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val profilePhoto: String,
    val fcmToken: String
)
