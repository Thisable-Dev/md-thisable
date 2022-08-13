package com.devtedi.tedi.data.remote.visionapi.model

data class TextDetectionRequest(
    val requests: TextDetectionRequestItem
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
