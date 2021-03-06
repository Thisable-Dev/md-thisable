package com.devthisable.thisable.data.source

import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.visionapi.VisionApiService
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionRequest
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VisionApiDataSource @Inject constructor(private val service: VisionApiService){

    suspend fun textDetection(apiKey: String, textDetectionRequest: TextDetectionRequest): Flow<ApiResponse<TextDetectionResponse>> {
        return flow {
            try {
                emit(ApiResponse.Loading)
                val response = service.textDetection(apiKey, textDetectionRequest)
                emit(ApiResponse.Success(response))
            } catch (ex: Exception) {
                emit(ApiResponse.Error(ex.message.toString()))
            }
        }
    }

}