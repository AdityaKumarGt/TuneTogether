package com.aditya.tune_together.data.repository

import android.util.Log
import com.aditya.tune_together.common.Resource
import com.aditya.tune_together.data.remote.BackendApi
import com.aditya.tune_together.data.remote.dto_register_user.RegisterUserRequest
import com.aditya.tune_together.data.remote.dto_register_user.toUser
import com.aditya.tune_together.data.remote.dto_userRooms.toUserRooms
import com.aditya.tune_together.domain.model.sign_in.User
import com.aditya.tune_together.domain.model.user_rooms.UserRoom
import com.aditya.tune_together.domain.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val backendApi: BackendApi,
    private val firebaseMessaging: FirebaseMessaging
) : UserRepository {
    override suspend fun sendCredentials(user: User): Flow<Resource<User>> {
        return flow {
            emit(Resource.Loading(true))
            try {
//                emit(Resource.Success(user))
//                Log.d("user", "User from Repo: $user")
//                emit(Resource.Loading(false))
                Log.d("user", "User from Repo: $user")
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                Log.d("fcmToken", "token: $fcmToken")

                val request = RegisterUserRequest(
                    uid = user.uid,
                    name = user.name,
                    email = user.email,
                    profilePhoto = user.profilePhoto,
                    fcmToken = fcmToken
                )
                val response = backendApi.registerUser(request)
                Log.d("user", "User from Repo, outapi: $user")
                if (response.userDetails != null) {
                    emit(Resource.Success(response.toUser()))
                    Log.d("user", "User from Repo inapi: $user")
                } else {
                    emit(Resource.Error("No user data returned"))
                }
                emit(Resource.Loading(false))

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("IOException: ${e.message}"))
            } catch (e: HttpException) {
                e.printStackTrace()
                val errorBody = e.response()?.errorBody()?.string() ?: "No error body"
                emit(Resource.Error("HttpException: ${e.message()} - Error Body: $errorBody"))
            }
        }
    }


    override suspend fun getUserRooms(uid: String): Flow<Resource<List<UserRoom>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val response = backendApi.getUserRooms(uid)
                if(response.userRooms.isEmpty()){
                    emit(Resource.Error("No rooms found"))
                    emit(Resource.Loading(true))
                    return@flow
                }else{
                    emit(Resource.Success(response.toUserRooms()))
                    emit(Resource.Loading(true))
                    Log.d("UserRooms", "Fetched ${response.userRooms.size} rooms")
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

}
