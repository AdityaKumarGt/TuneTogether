package com.aditya.tune_together.presentation.screens.room

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.tune_together.common.Resource
import com.aditya.tune_together.data.local.preferences.UserPreferenceManager
import com.aditya.tune_together.domain.model.room.Song
import com.aditya.tune_together.domain.repository.RoomRepository
import com.aditya.tune_together.domain.repository.UserRepository
import com.aditya.tune_together.presentation.screens.home_screen.UserRoomsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomViewModel @Inject constructor(
    private val roomRepository: RoomRepository,
) : ViewModel() {

    private val _messageList = MutableStateFlow<List<String>>(emptyList())
    val messageList: StateFlow<List<String>> = _messageList

    private val _roomState = MutableStateFlow<RoomState>(RoomState())
    val roomState: StateFlow<RoomState> = _roomState

    fun connectToRoom(roomId: String) {
        viewModelScope.launch {
            // Step 1: Start loading
            _roomState.value = _roomState.value.copy(
                isLoading = true,
//                error = null
            )

            roomRepository.getRoom(roomId).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _roomState.value = _roomState.value.copy(
                            room = result.data,
//                            isLoading = false,
                        )

                        // Step 2: Connect to socket
                        roomRepository.init(roomId, viewModelScope)

                        // Step 3: Start listening to messages
                        launch {
                            roomRepository.messages.collectLatest { message ->
                                _messageList.update { it + message } // append new message
                            }
                        }
                    }

                    is Resource.Error -> {
                        _roomState.value = _roomState.value.copy(
                            error = result.message ?: "Unknown error",
                            isLoading = false
                        )
                    }

                    is Resource.Loading -> {
                        _roomState.value = _roomState.value.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }


    fun sendMessage(message: String) {
        roomRepository.sendMessage(message)
    }

    fun leaveRoom() {
        roomRepository.disconnect()
    }





    //playerController:
    fun playSong(song: Song, currentPosition: Long){
        roomRepository.playSong(song, currentPosition)
    }
    fun pauseSong(){
        roomRepository.pauseSong()
    }
    fun seekTo(position: Long){
        roomRepository.seekTo(position)
    }
    fun changeSong(song: Song){
        roomRepository.changeSong(song)
    }
    fun isPlaying(){
        roomRepository.isPlaying()
    }
    fun getCurrentPosition(){
        roomRepository.getCurrentPosition()
    }
}

