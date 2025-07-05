package com.aditya.tune_together.presentation.screens.room

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aditya.tune_together.data.remote.WebSocketManager
import com.aditya.tune_together.domain.model.sign_in.User
import com.aditya.tune_together.presentation.screens.home_screen.UserRoomsViewModel

@Composable
fun RoomScreen(
    roomId: String,
    onLeaveRoom: () -> Unit, // pass navigation callback
    viewModel: RoomViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val roomState by viewModel.roomState.collectAsState()
    var isPlaying by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }
    val messageList by viewModel.messageList.collectAsState()

    // WebSocket connect
    LaunchedEffect(Unit) {
        viewModel.connectToRoom(roomId)
    }

    // Handle system back press
    BackHandler {
        showDialog = true
    }

    // Dialog when trying to leave room
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Leave Room?") },
            text = { Text("Are you sure you want to leave this room?") },
            confirmButton = {
                Button(onClick = {
                    viewModel.leaveRoom() // disconnect socket
                    showDialog = false
                    onLeaveRoom() // navigate to Home
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    // UI content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color(0xFF4A148C))
                )
            ),
//        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
//                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF4A148C))
                    .height(200.dp),
//                    .padding(50.dp),
                contentAlignment = Alignment.Center // 👈 this centers the content
            ) {
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Circular Button
                    Button(
                        onClick = {
                            val playJson = """
                {
                  "type": "play-song",
                  "payload": {
                    "roomId": "$roomId"
                  }
                }
            """.trimIndent()
//                            viewModel.sendMessage(playJson)
                            if(isPlaying){
                                viewModel.pauseSong()
                                isPlaying = !isPlaying
                            }else{
                                viewModel.playSong(roomState.room?.songs?.get(0)!!, 0)
                                isPlaying = !isPlaying
                            }


                        },
                        shape = CircleShape, // 👈 makes it circular
                        modifier = Modifier.size(100.dp), // adjust size
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White // background of button
                        )
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, // or Pause
                            contentDescription = "Play",
                            tint = Color(0xFF4A148C),
                            modifier = Modifier.size(60.dp) // icon size
                        )
                    }


                }
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 5.dp),
                    text = roomState.room?.songs?.getOrNull(0)?.name ?: "No song playing",
                    color = Color.White,
                    fontSize = 20.sp,
                )
            }


            //songs
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn {
                    roomState.room?.songs?.let { songs ->
                        items(songs) { song ->
                            Text(
                                text = song.name,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                //List of members:
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
//                                                .background(Color.Gray)
                        .padding(vertical = 16.dp)
                ) {
                    // Header Row: clickable title and expand/collapse icon
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Members",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = Color.White
                        )
                    }

                    // Animated visibility of members
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            roomState.room?.members?.forEach { member ->
                                Text(
                                    text = "${member.name ?: "Not added"} (${member.email})",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }


            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF4A148C))
                    .height(200.dp),
//                    .padding(50.dp),
                contentAlignment = Alignment.Center // 👈 this centers the content
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
//                        reverseLayout = true
                    ) {
                        items(messageList) { message ->
                            Text(
                                text = message,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                color = Color.White
                            )
                        }
                    }

                }
            }


        }

    }
}

