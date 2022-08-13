package com.devtedi.tedi.data.remote.auth.login

import com.devtedi.tedi.data.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("loginResult")
    val loginResult: User
)
