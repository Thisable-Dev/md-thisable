package com.devtedi.tedi.presentation.feature_cloud

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.devtedi.tedi.utils.MODEL_FILE_BISINDO
import com.devtedi.tedi.utils.MODEL_FILE_CURRENCY
import com.devtedi.tedi.utils.MODEL_FILE_OBJ
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import java.io.File


object CloudModel {

        private val modelCondition: CustomModelDownloadConditions =
            CustomModelDownloadConditions.Builder()
                .requireWifi()
                .build()

        var fileSignLanguage : File ?= null
        var fileCurrencyDetection : File?= null
        var fileObjectDetection : File?= null


        private val downloadType = DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND

         fun downloadObjectDetectionModel() : Boolean {

            FirebaseModelDownloader.getInstance()
                .getModel(MODEL_CLOUD_OD_NAME, downloadType, modelCondition)
                .addOnFailureListener {
                    failureObjectDetectionListener(it)
                }.addOnSuccessListener {
                    successObjectDetectionListener(it)
                }.addOnCanceledListener {
                    canceledObjectDetectionListener()
                }
             if(fileObjectDetection != null) return true
             return false

        }

         fun downloadCurrencyDetectionModel() : Boolean {
            FirebaseModelDownloader.getInstance()
                .getModel(MODEL_CLOUD_CD_NAME, downloadType, modelCondition)
                .addOnFailureListener {
                    failureCurrencyDetectionListener(it)
                }
                .addOnSuccessListener {
                    successCurrencyDetectionListener(it)
                }
                .addOnCanceledListener {
                    canceledCurrencyDetectionListener()
                }
             if(fileCurrencyDetection != null) return true
             return false
        }

         fun downloadSignLanguageModel() : Boolean {
            FirebaseModelDownloader.getInstance()
                .getModel(MODEL_CLOUD_SL_NAME, downloadType, modelCondition)
                .addOnFailureListener {
                    failureSignLanguageListener(it)
                }
                .addOnSuccessListener {
                    successSignLanguageListener(it)
                }
                .addOnCanceledListener {
                    canceledSignLanguageListener()
                }
                .addOnCompleteListener {
                }

             if(fileSignLanguage != null) return true
             return false

        }

        private fun failureObjectDetectionListener(it: Exception) {


        }

        private fun successObjectDetectionListener(it: CustomModel) {
            // Download succesfully
            val tempFile = it.file
            if (tempFile != null) {
                fileObjectDetection = it.file
            }
        }

        private fun canceledObjectDetectionListener() {


        }


        private fun failureCurrencyDetectionListener(it: Exception) {

        }

        private fun successCurrencyDetectionListener(it: CustomModel) {

            val tempFile = it.file
            if (tempFile != null) {
                fileCurrencyDetection = it.file
            }
        }

        private fun canceledCurrencyDetectionListener() {

        }

        private fun failureSignLanguageListener(it: Exception) {

        }

        private fun successSignLanguageListener(it: CustomModel) {

            val tempFile = it.file
            if (tempFile != null) {
                fileSignLanguage = it.file
            }
        }

        private fun canceledSignLanguageListener() {

        }
    private const val MODEL_CLOUD_SL_NAME : String = "SignlanguageModel"
    private const val MODEL_CLOUD_OD_NAME : String ="ObjectDetectionModel"
    private const val MODEL_CLOUD_CD_NAME : String = "CurrencyModel"

    }
