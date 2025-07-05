package com.aditya.tune_together.di

import android.app.Application
import com.aditya.tune_together.data.remote.BackendApi
import com.aditya.tune_together.data.repository.RoomRepositoryIml
import com.aditya.tune_together.data.repository.UserRepositoryImpl
import com.aditya.tune_together.domain.repository.RoomRepository
import com.aditya.tune_together.domain.repository.UserRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }

    @Provides
    fun provideAuthRepository(
        backendApi: BackendApi,
        firebaseMessaging: FirebaseMessaging,
    ): UserRepository {
        return UserRepositoryImpl(backendApi, firebaseMessaging)
    }

    @Provides
    fun provideRoomRepository(
        backendApi: BackendApi,
        application: Application
    ): RoomRepository {
        return RoomRepositoryIml(backendApi, application)
    }


}


