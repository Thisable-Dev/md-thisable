package com.devtedi.tedi.data.remote.general

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface GeneralService {

    @POST("report")
    @Multipart
    suspend fun addReportBug(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("message") message: RequestBody,
        @Part("severity") severity: RequestBody,
        @Part("phone_brand") phoneBrand: RequestBody,
        @Part("ram") ram: RequestBody,
        @Part("android_version") androidVersion: RequestBody,
        @Part photo: MultipartBody.Part,
        @Query("token") token: String,
    ): BugReportResponse

}