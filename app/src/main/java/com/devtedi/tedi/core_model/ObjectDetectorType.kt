package com.devtedi.tedi.core_model

import android.util.Size
import java.io.File


/**
 * Abstraksi untuk ObjectDetector Type
 * @property inputSize inputSizeModelObjectDetector
 * @property outputSize outputSizeModelObjectDetectorType
 * @property detect_threshold detectThreshold untuk objek yang terdeteksi
 * @property IOU_threshold IOU_Threshold untuk kalkulasi NMS
 * @property IOU_class_duplicated_threshold DUPLICATED_threshold untuk memdapatkan satu nilai terbaik untuk setiap label
 * @property label_file Lokasi dari label model
 * @property model_file Lokasi dari model file
 * @property IS_INT_8 Apakah model menggunakan .. Ahh lupa
 */


abstract class ObjectDetectorType {
    abstract var inputSize : Size
    abstract var outputSize : IntArray
    abstract var detect_threshold : Float
    abstract var IOU_threshold : Float
    abstract var IOU_class_duplicated_threshold : Float
    abstract var label_file : String
    abstract var model_file : File
    abstract var IS_INT_8 : Boolean

}