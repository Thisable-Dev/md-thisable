package com.bintangpoetra.thisable.data.source

import com.bintangpoetra.thisable.data.remote.ApiResponse
import com.bintangpoetra.thisable.data.remote.general.BugReportResponse
import com.bintangpoetra.thisable.data.remote.general.GeneralService
import com.bintangpoetra.thisable.data.source.report.ReportBugBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralDataSource @Inject constructor(private val service: GeneralService) {

    suspend fun addNewReportBug(reportBody: ReportBugBody): Flow<ApiResponse<BugReportResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = service.addReportBug(reportBody)
                if (response.error == "true") {
                    emit(ApiResponse.Error(response.message))
                } else {
                    emit(ApiResponse.Success(response))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }
}