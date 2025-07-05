package com.aditya.tune_together.presentation.screens.sign_in

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.tune_together.common.Resource
import com.aditya.tune_together.data.local.preferences.UserPreferenceManager
import com.aditya.tune_together.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: UserRepository,
    private val userPrefs: UserPreferenceManager,
) : ViewModel() {

    private val _state = MutableStateFlow<SignInState>(SignInState())
    val state: StateFlow<SignInState> = _state

    init {
        viewModelScope.launch {
            userPrefs.userFlow.collect { user ->
                _state.value = _state.value.copy(
                    isInitialized = true,
                    user = user
                )
            }
        }
    }

    fun signIn(
        context: Context,
        launcher: ManagedActivityResultLauncher<Intent, ActivityResult>?,
    ) {
        viewModelScope.launch {
            val user = GoogleSignInUtils.doGoogleSignIn(context, launcher)
//            val user = User("efksfh3sfk8ef", "Aditya", "aditya@gmail.com")
            Log.d("user", "User from VM: $user")
            user?.let {
                authRepository.sendCredentials(user).collect() { result ->
                    when (result) {
                        is Resource.Success -> {
//                            _state.value = _state.value.copy(user = result.data, isLoading = false)
                            userPrefs.saveUser(result.data!!)
                            _state.value = _state.value.copy(user = result.data, isLoading = false)
                        }

                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                error = result.message ?: "Unknown error",
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = _state.value.copy(isLoading = true)
                        }
                    }


                }


            }
        }


    }


    fun signOut() {
        viewModelScope.launch {
            userPrefs.clearUser()
            _state.value = _state.value.copy(user = null, isInitialized = false)
        }
    }

}

