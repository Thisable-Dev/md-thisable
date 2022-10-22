package com.devtedi.tedi.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import java.io.File

abstract class ModelTF(
    var inputSize: Size,
    var outputSize: IntArray,
    protected var detect_threshold : Float,
    protected var IOU_threshold : Float,
    protected var IOU_class_duplicated_threshold : Float,
    protected var label_file : String,
    protected var model_file: File,
    protected var IS_INT_8: Boolean = false
) {

    protected lateinit var tflite : Interpreter
    protected lateinit var associatedAxisLabels : List<String>
    protected var input5SINT8QuantParams: Tensor.QuantizationParams =
        Tensor.QuantizationParams(0.003921568859368563f, 0)
    protected var output5SINT8QuantParams: Tensor.QuantizationParams =
        Tensor.QuantizationParams(0.006305381190031767f, 5)

    protected var options = Interpreter.Options()
    abstract suspend fun create(context : Context) : YOLOv5ModelCreator
    abstract fun addGpuDelegate()
    abstract fun addNNApiDelegate()
    abstract suspend fun initialModel(activity : Context)
    abstract fun addThread(thread : Int)
    abstract fun detect(bitmap : Bitmap) : ArrayList<RecognitionRes>
    abstract fun close()
}