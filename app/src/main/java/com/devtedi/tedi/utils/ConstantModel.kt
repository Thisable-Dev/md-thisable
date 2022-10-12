package com.devtedi.tedi.utils

import android.util.Size

const val MODEL_FILE_OBJ: String = "ObjectDetectionModel.tflite"
const val MODEL_FILE_CURRENCY : String = "CurrencyModel.tflite"
const val MODEL_FILE_BISINDO : String ="SignLanguageModel.tflite"

const val LABEL_OBJ : String = "label_obj_detection.txt"
const val LABEL_CURRENCY : String = "label_currency.txt"
const val LABEL_COLOR : String = "file:///android_asset/color.txt"
const val LABEL_BISINDO : String = "label_sign_language.txt"

const val NUM_THREADS : Int = 2

const val default_img_size = 320
val MODEL_IMG_SIZE_obj : Size = Size(default_img_size, default_img_size)
val MODEL_IMG_SIZE_signLanguage: Size = Size(default_img_size, default_img_size)
val MODEL_IMG_SIZE_currency : Size = Size(default_img_size, default_img_size)

const val ISQUANTIZED : Boolean = false

const val const_object_detector : String = "obj_detector"
const val const_bisindo_translator : String = "bisindo_translator"
const val const_currency_detector : String = "currency_detector"
const val const_color_classifier : String = "color_classifier"


const val total_label_object : Int = 80;
const val total_label_bisindo : Int = 50;
const val total_label_currency : Int = 7;

const val DETECT_THRESHOLD : Float = 0.6f
const val IOU_THRESHOLD : Float = 0.45f
const val IOU_CLASS_DUPLICATED_THRESHOLD : Float = 0.7f
const val test_model : String = "yolov5n-fp16-320.tflite"
// Testing Variables
const val const_test_model : String= "test"
const val test_model_file : String = "best-fp16.tflite"
const val test_label_file : String = "file:///android_asset/customclasses.txt"
//const val TF_OD_API_INPUT_SIZE = 416
//const val TF_OD_API_IS_QUANTIZED = false
//const val TF_OD_API_MODEL_FILE = "best-fp16.tflite"
//const val TF_OD_API_LABELS_FILE = "file:///android_asset/customclasses.txt"

/// Constant Ywe
const val impl_oc_ocl_obj : String = "oc_ocl_obj"
const val impl_oc_ocl_currency : String = "oc_ocl_currency"
const val impl_oc_ocl_color : String = "oc_ocl_color"
const val impl_oc_ocl_text : String = "oc_ocl_text"

fun getObjConstTemp() : Array<String>
{
    return arrayOf("Ada Apa saja di depan saya ?", "Ada Meteor Bang", "Anjir Wibu")
}