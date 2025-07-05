package com.aditya.tune_together.data.remote

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.aditya.tune_together.domain.model.room.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONObject

class PlaybackController(
    context: Context,
    private val webSocketManager: WebSocketManager,
    private val scope: CoroutineScope,
    private val roomId: String,
) {

    private val player = ExoPlayer.Builder(context).build()

    init {
        observeSocketMessages()
    }

    private fun observeSocketMessages() {
        scope.launch {
            webSocketManager.messages.collectLatest { message ->
                handleSocketMessage(message)
            }
        }
    }

    private fun handleSocketMessage(message: String) {
        Log.d("PlaybackController", "Received message: $message")
        val json = JSONObject(message)
        val type = json.getString("type")

        when (type) {
            "play-song" -> {
                val payload = json.getJSONObject("payload")
                val url = payload.getString("songUrl")
                val timestamp = payload.getLong("timestamp")
                val serverTime = payload.getLong("serverTime")
                val latency = System.currentTimeMillis() - serverTime
                val seekTo = (timestamp + latency).coerceAtLeast(0)
                player.setMediaItem(MediaItem.fromUri(url))
                player.prepare()
                player.seekTo(seekTo)
                player.playWhenReady = true

                Log.d("PlaybackController", "Player is playing: ${player.isPlaying}")

            }

            "pause-song" -> player.pause()

            "resume-song" -> player.play()

            "seek-song" -> {
                val position = json.getJSONObject("payload").getLong("position")
                player.seekTo(position)
            }

            "change-song" -> {
                val url = json.getJSONObject("payload").getString("songUrl")
                player.setMediaItem(MediaItem.fromUri(url))
                player.prepare()
                player.playWhenReady = true
            }
        }
    }

    fun sendPlay(song: Song, currentPosition: Long) {
        val message = """
            {
              "type": "play-song",
              "payload": {
              "roomId": "$roomId",
                "songUrl": "${song.url}",
                "timestamp": $currentPosition,
                "serverTime": ${System.currentTimeMillis()}
              }
            }
        """.trimIndent()
        webSocketManager.send(message)
    }

    fun sendPause() {
        webSocketManager.send(
            """
            {
             "type": "pause-song",
               "payload": {
              "roomId": "$roomId"
              }
             }
             """.trimMargin()
        )
    }

    fun sendSeek(position: Long) {
        val message = """
            {
              "type": "seek-song",
              "payload": {
              "roomId": "$roomId",
               "position": $position
                }
            }
        """.trimIndent()
        webSocketManager.send(message)
    }

    fun sendChangeSong(song: Song) {
        val message = """
            {
              "type": "change-song",
              "payload": { 
              "songUrl": "${song.url}",
              "roomId": "$roomId"
               }
            }
        """.trimIndent()
        webSocketManager.send(message)
    }

    fun getCurrentPosition(): Long = player.currentPosition

    fun isPlaying(): Boolean = player.isPlaying

    fun release() {
        player.release()
    }
}
