package com.aditya.tune_together.data.remote.dto_register_user

import com.google.gson.annotations.SerializedName

data class UserDetails(
    @SerializedName("_id") val id: String,
    val name: String,
    val email: String,
    val fcmToken: String,
    val profilePhoto: String,


    )