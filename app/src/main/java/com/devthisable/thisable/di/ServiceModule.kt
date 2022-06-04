package com.devthisable.thisable.di

import com.devthisable.thisable.data.remote.auth.AuthService
import com.devthisable.thisable.data.remote.general.GeneralService
import com.devthisable.thisable.data.remote.visionapi.VisionApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    @Provides
    fun provideAuthService(@Named("General") retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    fun provideGeneralService(@Named("General") retrofit: Retrofit): GeneralService = retrofit.create(GeneralService::class.java)

    @Provides
    fun provideVisionAPIService(@Named("GoogleVisionAPI") retrofit: Retrofit) = retrofit.create(VisionApiService::class.java)

}