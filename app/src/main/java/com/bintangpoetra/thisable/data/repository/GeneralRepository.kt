package com.bintangpoetra.thisable.data.repository

import com.bintangpoetra.thisable.data.remote.ApiResponse
import com.bintangpoetra.thisable.data.remote.general.BugReportResponse
import com.bintangpoetra.thisable.data.source.GeneralDataSource
import com.bintangpoetra.thisable.data.source.report.ReportBugBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralRepository @Inject constructor(private val dataSource: GeneralDataSource) {

    suspend fun addNewReportBug(reportBugBody: ReportBugBody): Flow<ApiResponse<BugReportResponse>> {
        return dataSource.addNewReportBug(reportBugBody).flowOn(Dispatchers.IO)
    }

}