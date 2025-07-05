package com.aditya.tune_together.domain.repository

import com.aditya.tune_together.common.Resource
import com.aditya.tune_together.domain.model.sign_in.User
import com.aditya.tune_together.domain.model.user_rooms.UserRoom
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun sendCredentials(user: User): Flow<Resource<User>>

    suspend fun getUserRooms(uid: String): Flow<Resource<List<UserRoom>>>

}
