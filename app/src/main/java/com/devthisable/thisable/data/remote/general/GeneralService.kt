package com.devthisable.thisable.data.remote.general

import com.devthisable.thisable.data.remote.general.report.ReportBugBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeneralService {
    @POST("report")
    suspend fun addReportBug(
        @Body reportBugBody: ReportBugBody,
        @Query("token") token: String
    ): BugReportResponse
}