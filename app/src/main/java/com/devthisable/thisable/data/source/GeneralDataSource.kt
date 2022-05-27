package com.devthisable.thisable.data.source

import com.devthisable.thisable.BuildConfig
import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.general.BugReportResponse
import com.devthisable.thisable.data.remote.general.GeneralService
import com.devthisable.thisable.data.remote.general.report.ReportBugBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralDataSource @Inject constructor(private val service: GeneralService) {

    suspend fun addNewReportBug(reportBody: ReportBugBody): Flow<ApiResponse<BugReportResponse>> {
        return flow {
            val token = BuildConfig.API_KEY
            try {
                emit(ApiResponse.Loading)
                val response = service.addReportBug(reportBody, token)
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