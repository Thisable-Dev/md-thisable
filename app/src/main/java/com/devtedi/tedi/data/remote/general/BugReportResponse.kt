package com.devtedi.tedi.data.remote.general

import com.google.gson.annotations.SerializedName

data class BugReportResponse(
    @SerializedName("status")
    val status: String?,
    @SerializedName("error")
    val error: Boolean?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("data")
    val data: BugReportDataResponse?
)

data class BugReportDataResponse(
    @SerializedName("reportId")
    val reportId: String?
)
