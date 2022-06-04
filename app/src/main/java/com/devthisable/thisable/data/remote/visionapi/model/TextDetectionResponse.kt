package com.devthisable.thisable.data.remote.visionapi.model

import com.google.gson.annotations.SerializedName


data class TextDetectionResponse(
  @SerializedName("responses")
  var responses: List<ResponseItem>
)

data class ResponseItem(
  @SerializedName("fullTextAnnotation")
  var fullTextAnnotation: FullTextAnnotationItem,
  @SerializedName("error")
  val error: ErrorItem
)

data class FullTextAnnotationItem(
  @SerializedName("text")
  var text: String
)

data class ErrorItem(
  @SerializedName("code")
  var code: Int,
  @SerializedName("message")
  var message: String
)