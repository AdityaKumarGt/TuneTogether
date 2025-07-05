//package com.aditya.tune_together.data.remote
//
//import com.aditya.tune_together.domain.websocket.WebSocketService
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.SharedFlow
//import kotlinx.coroutines.launch
//import okhttp3.*
//import okio.ByteString
//
//class WebSocketManager(
//    private val roomId: String
//) : WebSocketListener(), WebSocketService {
//
//    private lateinit var webSocket: WebSocket
//    private val client = OkHttpClient()
//
//    private val _incomingMessages = MutableSharedFlow<String>()
//    override val incomingMessages: SharedFlow<String> = _incomingMessages
//
//    override fun connect() {
//        val request = Request.Builder()
//            .url("ws://10.0.2.2:4000") // Replace with wss://your-url in production
//            .build()
//
//        webSocket = client.newWebSocket(request, this)
//    }
//
//    override fun disconnect() {
//        if (::webSocket.isInitialized) {
//            webSocket.close(1000, "Client disconnected")
//        }
//    }
//
//    override fun sendMessage(message: String) {
//        if (::webSocket.isInitialized) {
//            webSocket.send(message)
//        }
//    }
//
//    override fun onOpen(webSocket: WebSocket, response: Response) {
//        println("✅ WebSocket connected")
//
//        val joinRoomJson = """
//            {
//              "type": "join-room",
//              "payload": {
//                "roomId": "$roomId"
//              }
//            }
//        """.trimIndent()
//
//        webSocket.send(joinRoomJson)
//    }
//
//    override fun onMessage(webSocket: WebSocket, text: String) {
//        println("📩 Received: $text")
//        // Emit to the SharedFlow
//        tryEmitMessage(text)
//    }
//
//    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//        println("📩 Received binary: $bytes")
//        tryEmitMessage(bytes.utf8())
//    }
//
//    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//        println("❌ WebSocket closed: $reason")
//    }
//
//    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//        println("🚫 WebSocket error: ${t.message}")
//    }
//
//    private fun tryEmitMessage(message: String) {
//        // Launching from global scope to avoid blocking socket thread
//        kotlinx.coroutines.GlobalScope.launch {
//            _incomingMessages.emit(message)
//        }
//    }
//}





//----------------------------------------------------------------------------------
//new one
package com.aditya.tune_together.data.remote

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*

class WebSocketManager(private val roomId: String) : WebSocketListener() {

    private val client = OkHttpClient()
    private lateinit var webSocket: WebSocket

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages

    fun connect() {
        val request = Request.Builder()
            .url("ws://10.0.2.2:4000")
            .build()

        webSocket = client.newWebSocket(request, this)
    }

    fun send(message: String) {
        webSocket.send(message)
    }

    fun close() {
        webSocket.close(1000, "Client disconnected")
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val joinRoomMessage = """
            {
              "type": "join-room",
              "payload": {
                "roomId": "$roomId"
              }
            }
        """.trimIndent()

        webSocket.send(joinRoomMessage)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        GlobalScope.launch {
            _messages.emit(text)
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("WebSocket Error: ${t.message}")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("WebSocket Closed: $reason")
    }
}






































































































//package com.aditya.tune_together.data.remote
//
//import okhttp3.*
//import okio.ByteString
//
//class WebSocketManager(
//    private val roomId: String,
//    private val onMessage: (String) -> Unit
//) : WebSocketListener() {
//
//    private lateinit var webSocket: WebSocket
//    private val client = OkHttpClient()
//
//    fun connect() {
//        val request = Request.Builder()
//            .url("ws://10.0.2.2:4000") // use wss:// in production
//            .build()
//
//        webSocket = client.newWebSocket(request, this)
//    }
//
//    override fun onOpen(webSocket: WebSocket, response: Response) {
//        println("✅ WebSocket connected")
//
//        val joinRoomJson = """
//            {
//              "type": "join-room",
//              "payload": {
//                "roomId": "$roomId"
//              }
//            }
//        """.trimIndent()
//
//        webSocket.send(joinRoomJson)
//    }
//
//    override fun onMessage(webSocket: WebSocket, text: String) {
//        println("📩 Received: $text")
//        onMessage(text)
//    }
//
//    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
//        println("❌ WebSocket closed: $reason")
//    }
//
//    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//        println("🚫 WebSocket error: ${t.message}")
//    }
//
//    fun sendMessage(json: String) {
//        webSocket.send(json)
//    }
//
//    fun disconnect() {
//        webSocket.close(1000, "Client disconnected")
//    }
//}
