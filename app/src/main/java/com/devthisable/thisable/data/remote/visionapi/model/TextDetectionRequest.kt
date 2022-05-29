package com.devthisable.thisable.data.remote.visionapi.model

data class TextDetectionRequest(
    val request: TextDetectionRequestItem
)

data class TextDetectionRequestItem(
    val image: ImageItem,
    val features: List<FeatureItem>
)

data class ImageItem(
    val content: String,
)

data class FeatureItem(
    val type: String,
    val maxResults: Int
)
