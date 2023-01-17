package com.devtedi.tedi.factory

import android.content.Context
import java.io.File

/**
    Model Store, ya ini simplenya untuk abstraksi kelas buat create model aja, biar lebih fleksibel untuk di add more change in the future
    Kelas ini di implement oleh ObjectDetectorStore
 *
 *
 */
abstract class ModelStore(protected val context: Context) {
    /**
     * Fungsi untuk membentuk model
     * @param type -> Nama Modelnya
     * @param filePath -> Lokasi dari file path modelnya
     */
    abstract suspend fun createModel(type: String, filePath : File): YOLOv5ModelCreator
}