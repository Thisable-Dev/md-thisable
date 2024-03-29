package com.devtedi.tedi.utils

import android.util.Size
import com.devtedi.tedi.R
/*
    Seluruh Constant untuk setiap  fitur machine learning, baiknya ntar di tarok aja di static field setiap fitur, Right now this is a bad habit

    // Simply cuman nilai ya, jadi tidak perlu dipersyulit

 */
val LABEL_USED_OBJ_PATH : String = StringBuilder().apply {
    append(ConstVal.ABSOLUTE_PATH)
    append(label_obj)
}.toString()

val LABEL_USED_CD_PATH : String = StringBuilder().apply {
    append(ConstVal.ABSOLUTE_PATH)
    append(label_cd)
}.toString()

val LABEL_USED_SL_PATH : String = StringBuilder().apply {
    append(ConstVal.ABSOLUTE_PATH)
    append(label_sl)
}.toString()

const val default_img_size = 320
val MODEL_IMG_SIZE_obj : Size = Size(default_img_size, default_img_size)
val MODEL_IMG_SIZE_signLanguage: Size = Size(default_img_size, default_img_size)
val MODEL_IMG_SIZE_currency : Size = Size(default_img_size, default_img_size)

const val ISQUANTIZED : Boolean = false

const val const_object_detector : String = "obj_detector"
const val const_bisindo_translator : String = "bisindo_translator"
const val const_currency_detector : String = "currency_detector"

const val total_label_object : Int = 80;
const val total_label_bisindo : Int = 50;
const val total_label_currency : Int = 7;

//const val DETECT_THRESHOLD : Float = 0.8f
//const val IOU_THRESHOLD : Float = 0.8f
//const val IOU_CLASS_DUPLICATED_THRESHOLD : Float = 0.85f
const val test_model : String = "yolov5n-fp16-320.tflite"
// Testing Variables
const val const_test_model : String= "test"
const val test_model_file : String = "best-fp16.tflite"
const val test_label_file : String = "file:///android_asset/customclasses.txt"

//const val TF_OD_API_INPUT_SIZE = 416
//const val TF_OD_API_IS_QUANTIZED = false
//const val TF_OD_API_MODEL_FILE = "best-fp16.tflite"
//const val TF_OD_API_LABELS_FILE = "file:///android_asset/customclasses.txt"

//Constant LabelFileName
const val label_cd : String = "label_CD.txt"
const val label_sl : String = "label_SL.txt"
const val label_obj : String = "label_OD.txt"

/// Constant Ywe
const val impl_oc_ocl_obj : String = "oc_ocl_obj"
const val impl_oc_ocl_currency : String = "oc_ocl_currency"
const val impl_oc_ocl_color : String = "oc_ocl_color"
const val impl_oc_ocl_text : String = "oc_ocl_text"

//constant for IOU model
const val IOU_THRESHOLD_object : Float = 0.8f
const val IOU_THRESHOLD_bisindo : Float = 0.9f
const val IOU_THRESHOLD_currency : Float = 0.8f

// Constant For DETECT THRESHOLD
const val DETECT_THRESHOLD_object : Float = 0.8f
const val DETECT_THRESHOLD_bisindo : Float= 0.9f
const val DETECT_THRESHOLD_currency : Float = 0.8f

//Constant for IOU CLASS DUPLICATED THRESHOLD
const val IOU_CLASS_DUPLICATED_THRESHOLD_object : Float = 0.8f;
const val IOU_CLASS_DUPLICATED_THRESHOLD_bisindo : Float = 0.9f;
const val IOU_CLASS_DUPLICATED_THRESHOLD_currency : Float = 0.85f