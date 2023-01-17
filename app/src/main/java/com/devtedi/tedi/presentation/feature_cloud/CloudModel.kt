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

/**
 * Kelas untuk Mengunduh model pada MLFirebase
 * @property observers ->
 * @property modelCondition -> Requirement / kebutuhan dalam melakukan pengunduhan pada BASE MODEL
 * @property modelConditionUpdateLatest -> Requirement / kebutuhan dalam pembaharuan model ( Belum digunakan di App Tedi saat ini)
 * @property MODEL_CLOUD_CD_NAME ->  Nama Model CurrencyDetector pada Cloud
 * @property MODEL_CLOUD_OD_NAME ->Nama Model ObjectDetector pada Cloud
 * @property MODEL_CLOUD_SL_NAME ->Nama Model Signlanguage pada Cloud
 * @property fileSignLanguage -> Just Empty File untuk hold signlanguage, if any
 * @property fileCurrencyDetection -> Just Empty File untuk hold CurrencyDetection, if any
 * @property fileObjectDetection -> Just Empty File untuk hold ObjectDetection, if any
 * @property isFileSignlanguageDownloaded-> Just Boolean to check whether the SignLanguageModel downloaded or not
 * @property isFileObjectDetectorDownloaded ->Just Boolean to check whether the ObjectDetectorModel downloaded or not
 * @property isFileCurrencyDetectorDownloaded ->Just Boolean to check whether the CurrencyDetectorModel downloaded or not
 * @property downloadType -> What type of download type you want, well simply as that
 * @property downloadTypeLatest -> Latest
 *
 * */
object CloudModel : CloudModelSubject {

    private val observers: ArrayList<CloudModelObserver> = ArrayList()
    private val modelCondition: CustomModelDownloadConditions =
        CustomModelDownloadConditions.Builder()
            .build()
    private val modelConditionUpdateLatest : CustomModelDownloadConditions = CustomModelDownloadConditions.Builder()
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
    private val downloadTypeLatest = DownloadType.LATEST_MODEL

    /**
     * Fungsi berfungsi untuk Download model Object detection saja
     * @return jika fileObjectDetection Tidak ditemukan pada perangkat user, maka download modelnya
     *
     * */
    @JvmStatic
    fun downloadObjectDetectionModel(): Boolean {

        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_CLOUD_OD_NAME, downloadType, modelCondition)
            .addOnFailureListener {
                if (it is FirebaseMlException) {
                    notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
                }
            }.addOnSuccessListener {
                successObjectDetectionListener(it)
            }.addOnCompleteListener {
                if (it.isSuccessful) {
                    completedObjectDetectionListener(false)
                }
            }
        if (fileObjectDetection != null) return true
        return false

    }

    /**
     * Fungsi berfungsi untuk Download model Currency detection saja
     * @return jika fileCurrencyDetection Tidak ditemukan pada perangkat user, maka download modelnya
     *
     * */
    @JvmStatic
    fun downloadCurrencyDetectionModel(): Boolean {
        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_CLOUD_CD_NAME, downloadType, modelCondition)
            .addOnFailureListener {
                if (it is FirebaseMlException) {
                    notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
                }
            }
            .addOnSuccessListener {
                successCurrencyDetectionListener(it)
            }
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    completedCurrencyDetectionListener(false)
                }

            }
        if (fileCurrencyDetection != null) return true
        return false
    }

    /**
     * Fungsi berfungsi untuk Download model SignLanguageModel
     * @return jika fileSignLanguage Tidak ditemukan pada perangkat user, maka download modelnya
     *
     * */
    @JvmStatic
    fun downloadSignLanguageModel(): Boolean {
        FirebaseModelDownloader.getInstance()
            .getModel(MODEL_CLOUD_SL_NAME, downloadType, modelCondition)
            .addOnFailureListener {
                if (it is FirebaseMlException) {
                    notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
                }
            }
            .addOnSuccessListener {
                successSignLanguageListener(it)
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completedSignLanguageListener(false)
                }
            }

        if (fileSignLanguage != null) return true
        return false
    }
    /**
     * Fungsi berfungsi untuk Download Latest model SignLanguageModel
     *
     * */
    fun downloadLatestSignlanguageModel() {
        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_SL_NAME, downloadTypeLatest, modelConditionUpdateLatest
        )
            .addOnFailureListener {
                if(it is FirebaseMlException)
                {
                    notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
                }
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val fileObj = it.result.file
                    if (fileObj != null) {
                        Log.d("DOWNLOADTAGS", "SignLanguageNewFileReplaced")
                        fileSignLanguage = fileObj
                        completedSignLanguageListener(true)
                    }
                }
            }
    }

    fun getTotalModels() : Int
    {
        FirebaseModelDownloader.getInstance()
        return 0;
    }

    /**
     * Fungsi berfungsi untuk Download Latest model ObjectDetector
     *
     * */
    fun downloadLatestObjectDetectionModel() {

        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_OD_NAME, downloadTypeLatest, modelConditionUpdateLatest
        )
            .addOnFailureListener {
                if(it is FirebaseMlException) {
                    notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
                }
            }
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val fileObj = it.result.file
                    if (fileObj != null) {
                        Log.d("DOWNLOADTAGS", "ObjectDetectionNewFileReplaced")
                        fileObjectDetection = fileObj
                        completedObjectDetectionListener(true)
                    }
                }
            }
    }

    /**
     * Fungsi berfungsi untuk Download Latest model CurrencyDetector
     *
     * */
    fun downloadLatestCurrencyDetectionModel() {
        FirebaseModelDownloader.getInstance().getModel(
            MODEL_CLOUD_CD_NAME, downloadTypeLatest, modelConditionUpdateLatest
        )
            .addOnFailureListener {
                if(it is FirebaseMlException) notifyObserverFailure(mapFirebaseMLExceptionToMessage(it.code))
            }
            .addOnCompleteListener {
                val fileObj = it.result.file
                if (fileObj != null) {
                    fileCurrencyDetection = fileObj
                    completedCurrencyDetectionListener(true)
                }
            }
    }

    /***
     * Fungsi untuk memberitahu Model CurrencyDetector DAPAT di unduh
     */
    private fun successObjectDetectionListener(it: CustomModel) {
        // Download succesfully
        val tempFile = it.file
        if (tempFile != null) {
            fileObjectDetection = it.file
        }

    }

    /***
     * Fungsi untuk memberitahu Model ObjectDetector SUDAH SELESAI di unduh
     */
    private fun completedObjectDetectionListener(isLatest: Boolean) {

        if (!isLatest) {
            isFileObjectDetectorDownloaded = true
            notifyObserver()
        } else {
            isFileObjectDetectorDownloaded = true
            notifyObserver()
        }
    }


    private fun mapFirebaseMLExceptionToMessage(code: Int): String {
        return when(code) {
            FirebaseMlException.UNAUTHENTICATED, FirebaseMlException.NO_NETWORK_CONNECTION  ->  "No internet connection, try again later."
            FirebaseMlException.NOT_ENOUGH_SPACE -> "Storage low, can't continue to download model. Please delete some unused file to continue."
            else -> "Can't continue to download model, try again later."
        }
    }

    /***
     * Fungsi untuk memberitahu Model CurrencyDetector DAPAT di unduh
     */
    private fun successCurrencyDetectionListener(it: CustomModel) {

        val tempFile = it.file
        if (tempFile != null) {
            fileCurrencyDetection = it.file
        }

    }

    /***
     * Fungsi untuk memberitahu Model CurrencyDetector SUDAH SELESAI di unduh
     */
    private fun completedCurrencyDetectionListener(isLatest: Boolean) {
        if (!isLatest) {
            isFileCurrencyDetectorDownloaded = true
            notifyObserver()
        } else {
            isFileCurrencyDetectorDownloaded = true
            notifyObserver()
        }
    }



    /***
     * Fungsi untuk memberitahu Model SignLanguageListener DAPAT di unduh
     */
    private fun successSignLanguageListener(it: CustomModel) {

        val tempFile = it.file
        if (tempFile != null) {
            fileSignLanguage = it.file
        }
    }
    /***
     * Fungsi untuk memberitahu Model Signlanguage SUDAH SELESAI di unduh
     */
    private fun completedSignLanguageListener(isLatest: Boolean) {
        if (!isLatest) {
            isFileSignlanguageDownloaded = true
            notifyObserver()
        }
        else {
            isFileSignlanguageDownloaded = true
            notifyObserver()
        }

    }


    //Delete model methods
    private fun deleteModel(modelName: String) {
        FirebaseModelDownloader.getInstance().deleteDownloadedModel(modelName)
    }

    override fun registerObserver(o: CloudModelObserver) {
        if(!observers.contains(o))
            observers.add(o)
    }

    override fun removeObserver(o: CloudModelObserver) {
        if(observers.contains(o))
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
