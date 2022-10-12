package com.devtedi.tedi.core_model

import android.util.Size

abstract class ObjectDetectorType {

    abstract var inputSize : Size
    abstract var outputSize : IntArray
    abstract var detect_threshold : Float
    abstract var IOU_threshold : Float
    abstract var IOU_class_duplicated_threshold : Float
    abstract var label_file : String
    abstract var model_file : String
    abstract var IS_INT_8 : Boolean


}