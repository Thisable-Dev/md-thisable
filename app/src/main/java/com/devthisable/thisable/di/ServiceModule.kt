package com.devthisable.thisable.di

import com.devthisable.thisable.data.remote.auth.AuthService
import com.devthisable.thisable.data.remote.general.GeneralService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    fun provideGeneralService(retrofit: Retrofit): GeneralService = retrofit.create(GeneralService::class.java)

}