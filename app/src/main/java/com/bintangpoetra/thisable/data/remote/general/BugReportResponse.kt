package com.bintangpoetra.thisable.data.remote.general

import com.google.gson.annotations.SerializedName

data class BugReportResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("message")
    val message: String
)
