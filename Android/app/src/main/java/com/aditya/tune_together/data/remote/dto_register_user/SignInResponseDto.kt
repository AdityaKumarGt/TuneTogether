package com.aditya.tune_together.data.remote.dto_register_user

import com.aditya.tune_together.domain.model.sign_in.User


data class SignInResponseDto(
    val message: String,
    val success: Boolean,
    val userDetails: UserDetails,

    )


fun SignInResponseDto.toUser(): User {
    return User(
        uid = userDetails.id,
        name = userDetails.name,
        email = userDetails.email,
        profilePhoto = userDetails.profilePhoto,
        fcmToken = userDetails.fcmToken

    )
}