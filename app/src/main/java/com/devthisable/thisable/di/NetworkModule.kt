package com.devthisable.thisable.di

import com.devthisable.thisable.utils.ConstVal.BASE_URL
import com.devthisable.thisable.utils.ConstVal.CLOUD_VISION_API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(150, TimeUnit.SECONDS)
            .readTimeout(150, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Named("General")
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    @Provides
    @Named("GoogleVisionAPI")
    fun provideRetrofitVisionApiService(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(CLOUD_VISION_API)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

}