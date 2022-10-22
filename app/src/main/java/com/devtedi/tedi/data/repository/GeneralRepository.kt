package com.devtedi.tedi.data.repository

import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.data.remote.general.BugReportResponse
import com.devtedi.tedi.data.source.GeneralDataSource
import com.devtedi.tedi.data.remote.general.report.ReportBugBody
import com.devtedi.tedi.data.remote.general.request.FileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralRepository @Inject constructor(private val dataSource: GeneralDataSource) {

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
        return dataSource.addNewReportBug(
            name,
            email,
            message,
            severity,
            phoneBrand,
            ram,
            androidVersion,
            file
        ).flowOn(Dispatchers.IO)
    }

}