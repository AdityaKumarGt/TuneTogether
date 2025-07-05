package com.aditya.tune_together.presentation.screens.home_screen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.aditya.tune_together.utils.UtilityFuctions.formatOffsetDateTime
import java.time.OffsetDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    uid: String, name: String, email: String, profilePhoto: String, fcmToken: String,
    onJoinRoom: (String) -> Unit,
    onSignOut: () -> Unit,
    viewModel: UserRoomsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val roomsState by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color(0xFF4A148C))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
//        verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Optional content on left


                Spacer(modifier = Modifier.weight(1f)) // Pushes the image to the right

                AsyncImage(
                    model = profilePhoto, // this is the URL
                    contentDescription = "Profile Photo",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .clickable {
                            onSignOut()

                        }
                )
            }

            // Ongoing Rooms
            if (roomsState.userRooms?.isNotEmpty() == true) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .background(Color.Transparent)
                        .fillMaxWidth()
                        .border(
                            width = 0.5.dp,
                            color = Color(0xFF4A148C) ,// Very deep purple
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Ongoing Rooms",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn {
                            roomsState.userRooms?.let { roomList ->
                                items(roomList) { room ->
                                    var isExpanded by remember { mutableStateOf(false) }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .background(
                                                Color(0xFF1C1C1E),
                                                shape = RoundedCornerShape(12.dp)
                                            )


                                            .padding(20.dp)
                                    ) {
                                        Text(
                                            text = room.roomName,
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Host: ${room.hostName} (${room.hostEmail})",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                        Row {
                                            Text(
                                                text = "Starts at: ${formatOffsetDateTime(room.startTime)}",
                                                color = Color.Gray,
                                                fontSize = 14.sp
                                            )

                                            if((OffsetDateTime.parse(room.startTime)).isBefore(OffsetDateTime.now())){
                                                Text(
                                                    text = " · Live",
                                                    color = Color.Green,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp
                                                )
                                            }

                                        }




                                        if (room.isCreatedByYou) {
                                            Text(
                                                text = "Created by you",
                                                color = Color.Green,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
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
                                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp  else Icons.Default.ArrowDropDown,
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
                                                    room.members.forEach { member ->
                                                        Text(
                                                            text = "${member.memberName} (${member.memberEmail})",
                                                            color = Color.Gray,
                                                            fontSize = 12.sp
                                                        )
                                                        Spacer(modifier = Modifier.height(5.dp))
                                                    }
                                                }
                                            }
                                        }

//                                        if((OffsetDateTime.parse(room.startTime)).isBefore(OffsetDateTime.now())){
                                        Button(
                                            onClick = {
                                        if((OffsetDateTime.parse(room.startTime)).isBefore(OffsetDateTime.now())){
                                            //Go to Room
                                            onJoinRoom(room.roomId)
                                        }else{
                                            Toast.makeText(context, "Room hasn't started yet!", Toast.LENGTH_SHORT).show()
                                        }

                                            },
                                            modifier = Modifier
//                                                .align(Alignment.BottomCenter)
                                                .padding( bottom = 10.dp)
                                                .fillMaxWidth(),
                                            colors = if((OffsetDateTime.parse(room.startTime)).isBefore(OffsetDateTime.now())){
                                                ButtonDefaults.buttonColors(
                                                    containerColor = Color.Blue,
                                                    contentColor = Color.White
                                                )
                                            }else{
                                                ButtonDefaults.buttonColors(
                                                     containerColor = Color.Black,
                                                    contentColor = Color.White
                                                )
                                            },





                                            shape = RoundedCornerShape(50.dp),
                                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                                        ) {

                                            Text(
                                                text = if((OffsetDateTime.parse(room.startTime)).isBefore(OffsetDateTime.now())){"Join Now"}else{"Join at: ${formatOffsetDateTime(room.startTime)}"},
                                                fontSize = 16.sp,
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    }

//                                    }
                                }
                            }



                    }
                }

            }
        }

        if (roomsState.error.isNotBlank()) {
            Text(
                text = roomsState.error,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.Center)
            )
        }


//        if (roomsState.isLoading) {
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//        }
    }
}

