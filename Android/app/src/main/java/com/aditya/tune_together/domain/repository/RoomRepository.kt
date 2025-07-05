package com.aditya.tune_together.domain.repository

import com.aditya.tune_together.common.Resource
import com.aditya.tune_together.domain.model.room.Room
import com.aditya.tune_together.domain.model.room.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface RoomRepository {
    suspend fun getRoom(roomId: String): Flow<Resource<Room>>

    //websocket function:
    fun init(roomId: String, scope: CoroutineScope)
    fun sendMessage(message: String)
    fun disconnect()
    val messages: SharedFlow<String>

    //playController:
    fun playSong(song: Song, currentPosition: Long)
    fun pauseSong()
    fun seekTo(position: Long)
    fun changeSong(song: Song)
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Long

}