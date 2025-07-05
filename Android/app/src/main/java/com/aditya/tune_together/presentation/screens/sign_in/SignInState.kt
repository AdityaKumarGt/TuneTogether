package com.aditya.tune_together.presentation.screens.sign_in

import com.aditya.tune_together.domain.model.sign_in.User


data class SignInState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String = "",
    val isInitialized: Boolean = false

)