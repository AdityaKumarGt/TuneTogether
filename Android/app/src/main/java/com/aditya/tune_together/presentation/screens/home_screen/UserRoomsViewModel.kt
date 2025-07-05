package com.aditya.tune_together.presentation.screens.home_screen

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
class UserRoomsViewModel @Inject constructor(
    private val authRepository: UserRepository,
    private val userPrefs: UserPreferenceManager,
) : ViewModel() {

    private val _state = MutableStateFlow<UserRoomsState>(UserRoomsState())
    val state: StateFlow<UserRoomsState> = _state

    init {
        viewModelScope.launch {
            userPrefs.userFlow.collect { user ->
                if (user != null) {
                    getUserRooms(user.uid)
                }
            }
        }
    }

    fun getUserRooms(uid: String) {
        viewModelScope.launch {
            authRepository.getUserRooms(uid).collect() { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(userRooms = result.data, isLoading = false)
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