package com.devthisable.thisable.data.repository

import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.general.BugReportResponse
import com.devthisable.thisable.data.source.GeneralDataSource
import com.devthisable.thisable.data.remote.general.report.ReportBugBody
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