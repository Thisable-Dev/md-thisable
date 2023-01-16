package com.devtedi.tedi.core_model

import android.util.Size
import java.io.File

/**
 * Konkrit Kelas untuk CurrencyDetector
 * Ya basically cuman store data saja
 * Dapat melihat Untuk penjelasannya
 * @see ObjectDetectorType
 *
 *
 */
class CurrencyDetector (
    override var inputSize: Size,
    override var outputSize: IntArray,
    override var detect_threshold: Float,
    override var IOU_threshold: Float,
    override var IOU_class_duplicated_threshold: Float,
    override var label_file: String,
    override var model_file: File,
    override var IS_INT_8: Boolean,
    )
    : ObjectDetectorType()