package com.devtedi.tedi.data.remote.cloudmodel

/**
 *
 * Kelas ini digunakan sebagai representasi Machine Learning model yang disimpan di cloud
 *
 * @property modelName nama model
 * @property modelVersion versi model
 * @property modelStatus status model (perlu update/tidak)
 * @constructor untuk buat instance dari CloudModelData.
 */
data class CloudModelData(
    val modelName : String,
    val modelVersion : String,
    val modelStatus : String
)