package com.devtedi.tedi.data.remote.general

import com.devtedi.tedi.BuildConfig
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface GeneralService {

    @POST("api/report/create")
    @Multipart
    suspend fun addReportBug(
        @Part("name") name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("message") message: RequestBody,
        @Part("severity") severity: RequestBody,
        @Part("PhoneBrand") phoneBrand: RequestBody,
        @Part("Ram") ram: RequestBody,
        @Part("AndroidVersion") androidVersion: RequestBody,
        @Part photo: MultipartBody.Part,
        @Header("x-access-token") token: String = BuildConfig.API_KEY
    ): BugReportResponse

}