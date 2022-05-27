package com.devthisable.thisable.data.repository

import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionRequest
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionResponse
import com.devthisable.thisable.data.source.VisionApiDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisionApiRepository @Inject constructor(private val dataSource: VisionApiDataSource) {

    suspend fun textDetection(apiKey: String, textDetectionRequest: TextDetectionRequest): Flow<ApiResponse<TextDetectionResponse>> {
        return dataSource.textDetection(apiKey, textDetectionRequest).flowOn(Dispatchers.IO)
    }
}