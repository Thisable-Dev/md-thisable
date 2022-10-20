package com.devtedi.tedi.presentation.feature_cloud

import com.devtedi.tedi.utils.MODEL_FILE_BISINDO
import com.devtedi.tedi.utils.MODEL_FILE_CURRENCY
import com.devtedi.tedi.utils.MODEL_FILE_OBJ
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader

class CloudModel {

    private lateinit var modelCondition: CustomModelDownloadConditions
    private val downloadType = DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND


    private fun setupDownloader() {

        modelCondition = CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

        downloadObjectDetectionModel()
        downloadCurrencyDetectionModel()
        downloadSignLanguageModel()

    }

    private fun downloadObjectDetectionModel() {
        FirebaseModelDownloader.getInstance().getModel(MODEL_FILE_OBJ, downloadType, modelCondition)
            .addOnFailureListener {
                failureObjectDetectionListener(it)
            }.addOnSuccessListener {
                successObjectDetectionListener(it)
            }.addOnCanceledListener {
                canceledObjectDetectionListener()
            }
    }

    private fun downloadCurrencyDetectionModel() {
        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_FILE_CURRENCY, downloadType, modelCondition)
            .addOnFailureListener {
                failureCurrencyDetectionListener(it)
            }
            .addOnSuccessListener {
                successCurrencyDetectionListener(it)
            }
            .addOnCanceledListener {
                canceledCurrencyDetectionListener()
            }
    }

    private fun downloadSignLanguageModel() {
        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_FILE_BISINDO, downloadType, modelCondition)
            .addOnFailureListener {
                failureSignLanguageListener(it)
            }
            .addOnSuccessListener {
                successSignLanguageListener(it)

            }
            .addOnCanceledListener {
                canceledSignLanguageListener()
            }
    }

    private fun failureObjectDetectionListener(it: Exception) {

    }

    private fun successObjectDetectionListener(it: CustomModel) {
        // Download succesfully
    }

    private fun canceledObjectDetectionListener() {

    }


    private fun failureCurrencyDetectionListener(it: Exception) {

    }

    private fun successCurrencyDetectionListener(it: CustomModel) {

    }

    private fun canceledCurrencyDetectionListener() {

    }

    private fun failureSignLanguageListener(it: Exception) {

    }

    private fun successSignLanguageListener(it: CustomModel) {

    }

    private fun canceledSignLanguageListener() {

    }
}
