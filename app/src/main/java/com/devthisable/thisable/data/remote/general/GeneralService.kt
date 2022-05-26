package com.devthisable.thisable.data.remote.general

import com.devthisable.thisable.data.source.report.ReportBugBody
import retrofit2.http.Body
import retrofit2.http.POST

interface GeneralService {
    @POST("report")
    suspend fun addReportBug(
        @Body reportBugBody: ReportBugBody
    ): BugReportResponse
}