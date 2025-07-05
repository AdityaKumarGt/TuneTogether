package com.aditya.tune_together.data.remote

import com.aditya.tune_together.data.remote.dto_register_user.RegisterUserRequest
import com.aditya.tune_together.data.remote.dto_register_user.SignInResponseDto
import com.aditya.tune_together.data.remote.dto_room.RoomDto
import com.aditya.tune_together.data.remote.dto_userRooms.UserRoomsResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BackendApi {
    @POST("api/v1/user/new")
    suspend fun registerUser(@Body user: RegisterUserRequest): SignInResponseDto

    @GET("api/v1/user/rooms")
    suspend fun getUserRooms(@Query("uid")uid: String): UserRoomsResponseDto

    @GET("api/v1/room")
    suspend fun getRoom(@Query("roomId")uid: String): RoomDto

}



