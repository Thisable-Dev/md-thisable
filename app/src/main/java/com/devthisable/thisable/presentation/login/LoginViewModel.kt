package com.devthisable.thisable.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.auth.login.LoginBody
import com.devthisable.thisable.data.remote.auth.login.LoginResponse
import com.devthisable.thisable.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {

    fun loginUser(loginBody: LoginBody): LiveData<ApiResponse<LoginResponse>> {
        val result = MutableLiveData<ApiResponse<LoginResponse>>()
        viewModelScope.launch {
            repository.loginUser(loginBody).collect {
                result.postValue(it)
            }
        }
        return result
    }

}