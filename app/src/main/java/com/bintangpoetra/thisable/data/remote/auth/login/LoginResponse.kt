package com.bintangpoetra.thisable.data.remote.auth.login

import com.bintangpoetra.thisable.data.model.User
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("loginResult")
    val loginResult: User
)
