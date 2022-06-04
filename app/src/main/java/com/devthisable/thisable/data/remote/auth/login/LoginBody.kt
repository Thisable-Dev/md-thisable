package com.devthisable.thisable.data.remote.auth.login

import com.google.gson.annotations.SerializedName

data class LoginBody(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String
)
