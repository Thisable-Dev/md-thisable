package com.devthisable.thisable.data.source

import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.auth.AuthService
import com.devthisable.thisable.data.remote.auth.login.LoginBody
import com.devthisable.thisable.data.remote.auth.login.LoginResponse
import com.devthisable.thisable.data.remote.auth.register.RegisterBody
import com.devthisable.thisable.data.remote.auth.register.RegisterResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataSource @Inject constructor(private val service: AuthService) {

    suspend fun registerUser(registerBody: RegisterBody): Flow<ApiResponse<RegisterResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = service.registerUser(registerBody)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

    suspend fun loginUser(loginBody: LoginBody): Flow<ApiResponse<LoginResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = service.loginUser(loginBody)
                if (!response.error) {
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error(response.message))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

}