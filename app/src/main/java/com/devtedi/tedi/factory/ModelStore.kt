package com.devtedi.tedi.factory

import android.content.Context

abstract class ModelStore(protected val context: Context) {
    abstract suspend fun createModel(type: String): YOLOv5ModelCreator
}