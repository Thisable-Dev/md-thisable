package com.devtedi.tedi.data.remote.auth

import com.devtedi.tedi.data.remote.auth.login.LoginBody
import com.devtedi.tedi.data.remote.auth.login.LoginResponse
import com.devtedi.tedi.data.remote.auth.register.RegisterBody
import com.devtedi.tedi.data.remote.auth.register.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {

    @POST("register")
    suspend fun registerUser(
        @Body authBody: RegisterBody
    ): RegisterResponse

    @POST("login")
    suspend fun loginUser(
        @Body loginBody: LoginBody
    ): LoginResponse

}