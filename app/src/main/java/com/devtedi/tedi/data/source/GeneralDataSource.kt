package com.devtedi.tedi.data.source

import com.devtedi.tedi.BuildConfig
import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.data.remote.general.BugReportResponse
import com.devtedi.tedi.data.remote.general.GeneralService
import com.devtedi.tedi.data.remote.general.report.ReportBugBody
import com.devtedi.tedi.data.remote.general.request.FileRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralDataSource @Inject constructor(private val service: GeneralService) {

    suspend fun addNewReportBug(reportBody: ReportBugBody, file: FileRequest): Flow<ApiResponse<BugReportResponse>> {
        return flow {
            val token = BuildConfig.API_KEY
            try {
                emit(ApiResponse.Loading)
                val response = service.addReportBug(reportBody, file, token)
                if (response.error!!) {
                    emit(ApiResponse.Error(response.message.toString()))
                } else {
                    emit(ApiResponse.Success(response))
                }
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }
}