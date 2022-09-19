package com.devtedi.tedi.data.remote.general.request

import com.google.gson.annotations.SerializedName

data class SpecificationBody(
    @SerializedName("phone_brand")
    val phoneBrand: String,
    @SerializedName("ram")
    val ram: Int,
    @SerializedName("android_version")
    val androidVersion: String
)