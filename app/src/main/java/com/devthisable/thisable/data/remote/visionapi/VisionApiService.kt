package com.devthisable.thisable.data.remote.visionapi

import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionRequest
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface VisionApiService {

    @POST("./images:annotate")
    suspend fun textDetection(
        @Query("key") apiKey: String,
        @Body textDetectionRequest: TextDetectionRequest
    ): TextDetectionResponse

}