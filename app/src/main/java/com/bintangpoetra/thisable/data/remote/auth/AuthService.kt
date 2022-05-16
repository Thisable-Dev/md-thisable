package com.bintangpoetra.thisable.data.remote.auth

import com.bintangpoetra.thisable.data.remote.auth.login.LoginBody
import com.bintangpoetra.thisable.data.remote.auth.login.LoginResponse
import com.bintangpoetra.thisable.data.remote.auth.register.RegisterBody
import com.bintangpoetra.thisable.data.remote.auth.register.RegisterResponse
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