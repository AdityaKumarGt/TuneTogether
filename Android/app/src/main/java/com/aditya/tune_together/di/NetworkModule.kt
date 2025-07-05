package com.aditya.tune_together.di

import com.aditya.tune_together.common.Constants
import com.aditya.tune_together.data.remote.BackendApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideBackendApi(retrofit: Retrofit): BackendApi {
        return retrofit.create(BackendApi::class.java)
    }
}
