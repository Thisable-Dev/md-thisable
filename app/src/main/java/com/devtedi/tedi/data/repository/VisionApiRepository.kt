package com.devtedi.tedi.data.repository

import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.data.remote.visionapi.model.TextDetectionRequest
import com.devtedi.tedi.data.remote.visionapi.model.TextDetectionResponse
import com.devtedi.tedi.data.source.VisionApiDataSource
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