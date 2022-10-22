package com.devtedi.tedi.data.source

import com.devtedi.tedi.BuildConfig
import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.data.remote.general.BugReportResponse
import com.devtedi.tedi.data.remote.general.GeneralService
import com.devtedi.tedi.data.remote.general.report.ReportBugBody
import com.devtedi.tedi.data.remote.general.request.FileRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralDataSource @Inject constructor(private val service: GeneralService) {

    suspend fun addNewReportBug(
        name: RequestBody,
        email: RequestBody,
        message: RequestBody,
        severity: RequestBody,
        phoneBrand: RequestBody,
        ram: RequestBody,
        androidVersion: RequestBody,
        file: MultipartBody.Part
    ): Flow<ApiResponse<BugReportResponse>> {
        return flow {
            val token = BuildConfig.API_KEY
            try {
                emit(ApiResponse.Loading)
                val response = service.addReportBug(
                    name,
                    email,
                    message,
                    severity,
                    phoneBrand,
                    ram,
                    androidVersion,
                    file,
                    token
                )
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