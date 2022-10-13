package com.devtedi.tedi.data.remote.general.report

import com.devtedi.tedi.data.remote.general.request.FileRequest
import com.devtedi.tedi.data.remote.general.request.SpecificationBody
import com.google.gson.annotations.SerializedName

data class ReportBugBody(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("severity")
    val severity: String,
    val specificationBody: SpecificationBody,
)
