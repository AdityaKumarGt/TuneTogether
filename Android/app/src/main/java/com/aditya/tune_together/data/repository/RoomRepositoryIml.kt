package com.aditya.tune_together.data.repository

import android.app.Application
import android.util.Log
import com.aditya.tune_together.common.Resource
import com.aditya.tune_together.data.remote.BackendApi
import com.aditya.tune_together.data.remote.PlaybackController
import com.aditya.tune_together.data.remote.WebSocketManager
import com.aditya.tune_together.data.remote.dto_room.toRoom
import com.aditya.tune_together.data.remote.dto_userRooms.toUserRooms
import com.aditya.tune_together.domain.model.room.Room
import com.aditya.tune_together.domain.model.room.Song
import com.aditya.tune_together.domain.repository.RoomRepository
import com.aditya.tune_together.domain.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RoomRepositoryIml @Inject constructor(
    private val backendApi: BackendApi,
    private val application: Application, // to pass context

) : RoomRepository {
    override suspend fun getRoom(roomId: String): Flow<Resource<Room>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = backendApi.getRoom(roomId)
                if (response.room == null) {
                    emit(Resource.Error("Room not found!"))
                    emit(Resource.Loading(true))
                    return@flow
                } else {
                    emit(Resource.Success(response.toRoom()))
                    emit(Resource.Loading(true))
                }
            } catch (e: IOException) {
                emit(Resource.Error("IOException: ${e.message}"))
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "No error body"
                emit(Resource.Error("HttpException: ${e.message()} - $errorBody"))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    //WebSocket methods:----------------------------------
    private var webSocketManager: WebSocketManager? = null
    private var playbackController: PlaybackController? = null

    override fun init(roomId: String, scope: CoroutineScope) {
        webSocketManager = WebSocketManager(roomId).apply { connect() }
        // Initialize PlaybackController only once
        if (playbackController == null) {
            playbackController = PlaybackController(
                context = application.applicationContext,
                webSocketManager!!,
                scope,
                roomId

            )
        }
    }

        override fun sendMessage(message: String) {
            webSocketManager?.send(message)
        }

        override fun disconnect() {
            webSocketManager?.close()
        }

        override val messages: SharedFlow<String>
        get() = webSocketManager?.messages ?: error("WebSocket not initialized")


        // -------------------- Playback Control API ------------------------
        override fun playSong(song: Song, currentPosition: Long) {
            playbackController?.sendPlay(song, currentPosition)
        }

        override fun pauseSong() {
            playbackController?.sendPause()
        }

        override fun seekTo(position: Long) {
            playbackController?.sendSeek(position)
        }

        override fun changeSong(song: Song) {
            playbackController?.sendChangeSong(song)
        }

        override fun isPlaying(): Boolean {
            return playbackController?.isPlaying() ?: false
        }

        override fun getCurrentPosition(): Long {
            return playbackController?.getCurrentPosition() ?: 0L
        }

    }