package com.devtedi.tedi.factory

import android.content.Context
import com.devtedi.tedi.core_model.BisindoTranslator
import com.devtedi.tedi.core_model.CurrencyDetector
import com.devtedi.tedi.core_model.ObjectDetector
import com.devtedi.tedi.utils.*
import java.io.File

class ObjectDetectorStore(context: Context) : ModelStore(context) {
    override suspend fun createModel(type: String, filePath : File): YOLOv5ModelCreator {
        when (type) {
            const_object_detector -> {

                val objectDetector = ObjectDetector(
                    MODEL_IMG_SIZE_obj,
                    UtilsClassifier.calculateOutputSize(default_img_size, total_label_object),
                    DETECT_THRESHOLD_object,
                    IOU_THRESHOLD_object,
                    IOU_CLASS_DUPLICATED_THRESHOLD_object,
                    LABEL_USED_OBJ_PATH, filePath,
                    false)

                val yolOv5ModelCreator = YOLOv5ModelCreator(
                    objectDetector.inputSize, objectDetector.outputSize,
                    objectDetector.detect_threshold, objectDetector.IOU_threshold,
                    objectDetector.IOU_class_duplicated_threshold, objectDetector.label_file,
                    objectDetector.model_file, objectDetector.IS_INT_8
                )
                return yolOv5ModelCreator.create(context)
            }
            const_bisindo_translator -> {

                val bisindoTranslator =
                    BisindoTranslator(
                        MODEL_IMG_SIZE_signLanguage,
                        UtilsClassifier.calculateOutputSize(default_img_size, total_label_bisindo),
                        DETECT_THRESHOLD_bisindo,
                        IOU_THRESHOLD_bisindo,
                        IOU_CLASS_DUPLICATED_THRESHOLD_bisindo,
                        LABEL_USED_SL_PATH, filePath,
                        false)

                val yolov5ModelCreator = YOLOv5ModelCreator(
                    bisindoTranslator.inputSize, bisindoTranslator.outputSize,
                    bisindoTranslator.detect_threshold, bisindoTranslator.IOU_threshold,
                    bisindoTranslator.IOU_class_duplicated_threshold, bisindoTranslator.label_file,
                    bisindoTranslator.model_file, bisindoTranslator.IS_INT_8)

                return yolov5ModelCreator.create(context)
            }

            else -> {
                val currencyDetector =
                    CurrencyDetector(
                        MODEL_IMG_SIZE_currency,
                        UtilsClassifier.calculateOutputSize(default_img_size, total_label_currency),
                        DETECT_THRESHOLD_currency,
                        IOU_THRESHOLD_currency,
                        IOU_CLASS_DUPLICATED_THRESHOLD_currency,
                        LABEL_USED_CD_PATH, filePath,
                        false
                    )

                val yolOv5ModelCreator = YOLOv5ModelCreator(
                    currencyDetector.inputSize, currencyDetector.outputSize,
                    currencyDetector.detect_threshold, currencyDetector.IOU_threshold,
                    currencyDetector.IOU_class_duplicated_threshold, currencyDetector.label_file,
                    currencyDetector.model_file,currencyDetector.IS_INT_8
                )

                return yolOv5ModelCreator.create(context)
            }
        }
    }
}