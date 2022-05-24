package com.bintangpoetra.thisable.data.remote.general

import com.bintangpoetra.thisable.data.source.report.ReportBugBody
import retrofit2.http.Body
import retrofit2.http.POST

interface GeneralService {
    @POST("report")
    suspend fun addReportBug(
        @Body reportBugBody: ReportBugBody
    ): BugReportResponse
}