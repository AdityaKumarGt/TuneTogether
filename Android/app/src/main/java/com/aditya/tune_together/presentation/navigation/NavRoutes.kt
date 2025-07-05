package com.aditya.tune_together.presentation.navigation

 import kotlinx.serialization.Serializable
@kotlinx.serialization.Serializable

object SignIn


@Serializable
data class Home(
 val uid: String,
 val name: String,
 val email: String,
 val profilePhoto: String,
  val fcmToken: String
)

@Serializable
data class Room(
 val roomId: String,
)