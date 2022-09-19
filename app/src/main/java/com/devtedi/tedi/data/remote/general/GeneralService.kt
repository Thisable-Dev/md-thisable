package com.devtedi.tedi.data.remote.general

import com.devtedi.tedi.data.remote.general.report.ReportBugBody
import com.devtedi.tedi.data.remote.general.request.FileRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface GeneralService {
    @POST("report")
    suspend fun addReportBug(
        @Body reportBugBody: ReportBugBody,
//        @Part file: FileRequest,
        @Query("token") token: String
    ): BugReportResponse
}