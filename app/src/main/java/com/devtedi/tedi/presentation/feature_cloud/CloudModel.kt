package com.devtedi.tedi.presentation.feature_cloud

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import com.devtedi.tedi.interfaces.observer_cloud.CloudModelObserver
import com.devtedi.tedi.interfaces.observer_cloud.CloudModelSubject
import com.devtedi.tedi.utils.MODEL_FILE_BISINDO
import com.devtedi.tedi.utils.MODEL_FILE_CURRENCY
import com.devtedi.tedi.utils.MODEL_FILE_OBJ
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import okhttp3.internal.notify
import java.io.File


object CloudModel : CloudModelSubject {

    private val observers : ArrayList<CloudModelObserver> = ArrayList()
    private val modelCondition: CustomModelDownloadConditions =
        CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

    private const val MODEL_CLOUD_SL_NAME : String = "SignlanguageModel"
    private const val MODEL_CLOUD_OD_NAME : String ="ObjectDetectionModel"
    private const val MODEL_CLOUD_CD_NAME : String = "CurrencyModel"

    var fileSignLanguage : File ?= null
    var fileCurrencyDetection : File?= null
    var fileObjectDetection : File?= null

    private var isFileSignlanguageDownloaded : Boolean = false
    private var isFileCurrencyDetectorDownloaded : Boolean = false
    private var isFileObjectDetectorDownloaded : Boolean = false

    private val downloadType = DownloadType.LOCAL_MODEL
    private val downloadTypeLatest = DownloadType.LATEST_MODEL

    fun downloadObjectDetectionModel() : Boolean {

        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_CLOUD_OD_NAME, downloadType, modelCondition)
            .addOnFailureListener {
                failureObjectDetectionListener(it)
            }.addOnSuccessListener {
                successObjectDetectionListener(it)
            }.addOnCanceledListener {
                canceledObjectDetectionListener()
            }.addOnCompleteListener{

                if(it.isSuccessful) {
                    completedObjectDetectionListener()
                }
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
            .addOnCompleteListener {

                if(it.isSuccessful) {
                    completedCurrencyDetectionListener()
                }
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
                if(it.isSuccessful) {
                    completedSignLanguageListener()
                }
            }

        if(fileSignLanguage != null) return true
        return false
    }

    private fun downloadLatestSignlanguageModel()
    {
        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_SL_NAME, downloadTypeLatest, modelCondition
        )
            .addOnFailureListener {

            }
            .addOnSuccessListener {

            }
            .addOnCanceledListener {

            }
            .addOnCompleteListener {  }
    }

    private fun downloadLatestObjectDetectionModel()
    {

        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_OD_NAME, downloadTypeLatest, modelCondition
        )
            .addOnFailureListener {

            }
            .addOnSuccessListener {

            }
            .addOnCanceledListener {

            }
            .addOnCompleteListener {  }
    }

    private fun downloadLatestCurrencyDetectionModel()
    {

        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_CD_NAME, downloadTypeLatest, modelCondition
        )
            .addOnFailureListener {

            }
            .addOnSuccessListener {

            }
            .addOnCanceledListener {

            }
            .addOnCompleteListener {  }
    }

    private fun failureObjectDetectionListener(it: Exception) {

        Log.d("DOWNLOADTAGS", it.message.toString())

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

    private fun completedObjectDetectionListener ()
    {
        isFileObjectDetectorDownloaded = true
        notifyObserver()
    }


    private fun failureCurrencyDetectionListener(it: Exception) {

        Log.d("DOWNLOADTAGS", it.message.toString())
    }

    private fun successCurrencyDetectionListener(it: CustomModel) {

        val tempFile = it.file
        if (tempFile != null) {
            fileCurrencyDetection = it.file
        }

    }

    private fun canceledCurrencyDetectionListener() {

    }

    private fun completedCurrencyDetectionListener()
    {
        isFileCurrencyDetectorDownloaded = true
        notifyObserver()
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

    private fun completedSignLanguageListener()
    {
        isFileSignlanguageDownloaded = true
        notifyObserver()
    }


    override fun registerObserver(o: CloudModelObserver) {
        observers.add(o)
    }

    override fun removeObserver(o: CloudModelObserver) {
        observers.remove(o)
    }

    override fun notifyObserver() {
        for(o in observers)
        {
            o.updateObserver()
        }
    }

}
