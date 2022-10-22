package com.devtedi.tedi.factory

import android.content.Context
import java.io.File

abstract class ModelStore(protected val context: Context) {
    abstract suspend fun createModel(type: String, filePath : File): YOLOv5ModelCreator
}