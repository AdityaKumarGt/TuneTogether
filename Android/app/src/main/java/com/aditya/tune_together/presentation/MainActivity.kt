package com.aditya.tune_together.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.aditya.tune_together.presentation.ui.theme.TuneTogetherTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.aditya.tune_together.presentation.navigation.Home
import com.aditya.tune_together.presentation.navigation.Room
import com.aditya.tune_together.presentation.navigation.SignIn
import com.aditya.tune_together.presentation.screens.home_screen.HomeScreen
import com.aditya.tune_together.presentation.screens.room.RoomScreen
import com.aditya.tune_together.presentation.screens.sign_in.AuthViewModel
import com.aditya.tune_together.presentation.screens.sign_in.SignInScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            TuneTogetherTheme {
                val navController = rememberNavController()
                val viewModel: AuthViewModel = hiltViewModel()
                val userState by viewModel.state.collectAsState()

                // Determine start destination based on whether user is signed in
                if (!userState.isInitialized) {
                    // splash screen or nothing
                } else {
                    val navController = rememberNavController()
                    val startDestination = remember(userState.user) {
                        if (userState.user != null)
                            Home(
                                userState.user!!.uid,
                                userState.user!!.name,
                                userState.user!!.email,
                                userState.user!!.profilePhoto,
                                userState.user!!.fcmToken
                            )
                        else SignIn
                    }

                    NavHost(navController, startDestination = startDestination) {
                        composable<SignIn> {
                            SignInScreen(onSignedIn = { user ->
//                            navController.navigate(Home(user.email))
                                navController.navigate(
                                    Home(
                                        user.uid,
                                        user.name,
                                        user.email,
                                        user.profilePhoto,
                                        user.fcmToken
                                    )
                                )


                            })
                        }
                        composable<Home> { backStackEntry ->
                            val home = backStackEntry.toRoute<Home>()
                            HomeScreen(home.uid, home.name, home.email, home.profilePhoto, home.fcmToken,
                                onJoinRoom = { roomId ->
                                    navController.navigate(Room(roomId))
                                },
                                onSignOut = {
                                    viewModel.signOut()
                                    navController.navigate(SignIn)
                                }
                                )

                        }

                        composable<Room> { backStackEntry ->
                            val room = backStackEntry.toRoute<Room>()
                            RoomScreen(
                                room.roomId,
                                onLeaveRoom = {
                                    navController.popBackStack() // ⬅️ go back to Home
                                }
                                )
                        }
                    }


                }
            }
        }
    }
}





































