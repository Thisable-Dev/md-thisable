package com.devthisable.thisable.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.auth.register.RegisterBody
import com.devthisable.thisable.data.remote.auth.register.RegisterResponse
import com.devthisable.thisable.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {

    fun registerUser(registerBody: RegisterBody): LiveData<ApiResponse<RegisterResponse>> {
        val result = MutableLiveData<ApiResponse<RegisterResponse>>()
        viewModelScope.launch {
            repository.registerUser(registerBody).collect {
                result.postValue(it)
            }
        }
        return result
    }

}