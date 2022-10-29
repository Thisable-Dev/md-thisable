package com.devtedi.tedi.presentation.feature_cloud

import android.util.Log
import com.devtedi.tedi.interfaces.observer_cloud.CloudModelObserver
import com.devtedi.tedi.interfaces.observer_cloud.CloudModelSubject
import com.google.firebase.ml.modeldownloader.CustomModel
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions
import com.google.firebase.ml.modeldownloader.DownloadType
import com.google.firebase.ml.modeldownloader.FirebaseMlException
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader
import java.io.File


object CloudModel : CloudModelSubject {

    private val observers: ArrayList<CloudModelObserver> = ArrayList()
    private val modelCondition: CustomModelDownloadConditions =
        CustomModelDownloadConditions.Builder()
            .requireWifi()
            .build()

    private const val MODEL_CLOUD_SL_NAME: String = "SignlanguageModel"
    private const val MODEL_CLOUD_OD_NAME: String = "ObjectDetectionModel"
    private const val MODEL_CLOUD_CD_NAME: String = "CurrencyModel"

    var fileSignLanguage: File? = null
    var fileCurrencyDetection: File? = null
    var fileObjectDetection: File? = null

    private var isFileSignlanguageDownloaded: Boolean = false
    private var isFileCurrencyDetectorDownloaded: Boolean = false
    private var isFileObjectDetectorDownloaded: Boolean = false

    private val downloadType = DownloadType.LOCAL_MODEL
    private val downloadTypeLatest = DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND

    @JvmStatic
    fun downloadObjectDetectionModel(): Boolean {

        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_CLOUD_OD_NAME, downloadType, modelCondition)
            .addOnFailureListener {
                if (it is FirebaseMlException) {
                    notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
                }
                failureObjectDetectionListener(it)
            }.addOnSuccessListener {
                successObjectDetectionListener(it)
            }.addOnCanceledListener {
                canceledObjectDetectionListener()
            }.addOnCompleteListener {

                if (it.isSuccessful) {
                    completedObjectDetectionListener(false)
                }
            }
        if (fileObjectDetection != null) return true
        return false

    }

    @JvmStatic
    fun downloadCurrencyDetectionModel(): Boolean {
        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_CLOUD_CD_NAME, downloadType, modelCondition)
            .addOnFailureListener {
                if (it is FirebaseMlException) {
                    notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
                }
                failureCurrencyDetectionListener(it)
            }
            .addOnSuccessListener {
                successCurrencyDetectionListener(it)
            }
            .addOnCanceledListener {
                canceledCurrencyDetectionListener()
            }
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    completedCurrencyDetectionListener(false)
                }
            }
        if (fileCurrencyDetection != null) return true
        return false
    }

    @JvmStatic
    fun downloadSignLanguageModel(): Boolean {
        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_CLOUD_SL_NAME, downloadType, modelCondition)
            .addOnFailureListener {
                if (it is FirebaseMlException) {
                    notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
                }
                failureSignLanguageListener(it)
            }
            .addOnSuccessListener {
                successSignLanguageListener(it)
            }
            .addOnCanceledListener {
                canceledSignLanguageListener()
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completedSignLanguageListener(false)
                }
            }

        if (fileSignLanguage != null) return true
        return false
    }


    fun downloadLatestSignlanguageModel() {
        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_SL_NAME, downloadTypeLatest, modelCondition
        )
            .addOnFailureListener {

            }
            .addOnSuccessListener {

            }
            .addOnCanceledListener {

            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completedSignLanguageListener(true)
                    val fileObj = it.result.file
                    if (fileObj != null) {

                        Log.d("DOWNLOADTAGS", "SignlanguageNewFileReplaced")
                        fileObjectDetection = fileObj
                    }
                }
            }
    }

    fun downloadLatestObjectDetectionModel() {

        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_OD_NAME, downloadTypeLatest, modelCondition
        )
            .addOnFailureListener {

            }
            .addOnSuccessListener {

            }
            .addOnCanceledListener {

            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completedObjectDetectionListener(true)
                    val fileObj = it.result.file
                    if (fileObj != null) {
                        Log.d("DOWNLOADTAGS", "ObjectDetectionNewFileReplaced")
                        fileObjectDetection = fileObj
                    }
                }
            }
    }

    fun downloadLatestCurrencyDetectionModel() {
        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_CD_NAME, downloadTypeLatest, modelCondition
        )
            .addOnFailureListener {

            }
            .addOnSuccessListener {

            }
            .addOnCanceledListener {

            }
            .addOnCompleteListener {
                completedCurrencyDetectionListener(true)
                val fileObj = it.result.file
                if (fileObj != null) {
                    Log.d("DOWNLOADTAGS", "CurrencyNewFileReplaced")
                    fileCurrencyDetection = fileObj
                }
            }
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

    private fun completedObjectDetectionListener(isLatest: Boolean) {

        if (!isLatest) {
            isFileObjectDetectorDownloaded = true
            notifyObserver()
        } else {
            //Log.d("DOWNLOADTAGS", "Deleted the $fileObjectDetection")
            //deleteModel(MODEL_CLOUD_OD_NAME)
        }
    }


    private fun failureCurrencyDetectionListener(it: Exception) {
        Log.d("DOWNLOADTAGS", it.message.toString())
    }

    private fun mapFirebaseMLExceptionToMessage(code: Int): String {
        return when(code) {
            FirebaseMlException.UNAUTHENTICATED, FirebaseMlException.NO_NETWORK_CONNECTION  ->  "No internet connection, try again later."
            FirebaseMlException.NOT_ENOUGH_SPACE -> "Storage low, can't continue to download model. Please delete some unused file to continue."
            else -> "Can't continue to download model, try again later."
        }
    }

    private fun successCurrencyDetectionListener(it: CustomModel) {


        val tempFile = it.file
        if (tempFile != null) {
            fileCurrencyDetection = it.file
        }

    }

    private fun canceledCurrencyDetectionListener() {

    }

    private fun completedCurrencyDetectionListener(isLatest: Boolean) {
        if (!isLatest) {
            isFileCurrencyDetectorDownloaded = true
            notifyObserver()
        } else {
            // Do another Kek Delete Model
            //Log.d("DOWNLOADTAGS", "Deleted the $fileCurrencyDetection")
            //deleteModel(MODEL_CLOUD_CD_NAME)
        }
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

    private fun completedSignLanguageListener(isLatest: Boolean) {
        if (!isLatest) {
            isFileSignlanguageDownloaded = true
            notifyObserver()
        } else {
            //Log.d("DOWNLOADTAGS", "Deleted the $fileSignLanguage")
            // Remove Model
            //deleteModel(MODEL_CLOUD_SL_NAME)
        }

    }


    //Delete model methods
    private fun deleteModel(modelName: String) {
        FirebaseModelDownloader.getInstance().deleteDownloadedModel(modelName)
    }

    override fun registerObserver(o: CloudModelObserver) {
        observers.add(o)
    }

    override fun removeObserver(o: CloudModelObserver) {
        observers.remove(o)
    }

    override fun notifyObserver() {
        for (o in observers) {
            o.updateObserver()
        }
    }

    override fun notifyObserverFailure(message: String) {
        for (o in observers) {
            o.updateFailureObserver(message)
        }
    }
}
