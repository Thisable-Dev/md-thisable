package com.devtedi.tedi.presentation.feature_cloud

import android.util.Log
import com.devtedi.tedi.BuildConfig
import com.devtedi.tedi.interfaces.observer_cloudstorage.CloudStorageObserver
import com.devtedi.tedi.interfaces.observer_cloudstorage.CloudStorageSubject
import com.devtedi.tedi.utils.ConstVal
import com.devtedi.tedi.utils.isEndWithTxt
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path

object CloudStorage : CloudStorageSubject {

    private const val PREFIX_SL: String = "label_SL"
    private const val PREFIX_OD: String = "label_OD"
    private const val PREFIX_CD: String = "label_CD"

    private const val SUFFIX_ALL: String = ".txt"

    var labelFileSignLanguage: String? = null
    var labelFileCurrencyDetection: String? = null
    var labelFileObjectDetection: String? = null

    private lateinit var storage: FirebaseStorage

    private val observers: ArrayList<CloudStorageObserver> = ArrayList()

    fun getLabelFilesFromCloud() {
        storage = FirebaseStorage.getInstance(BuildConfig.FIREBASE_STORAGE_PATH)
        downloadLabelFiles()
    }

    private fun downloadLabelFiles() {
        getTheObjectDetectionFile()
        getTheCurrencyDetectionFile()
        getTheSignLanguageFile()
    }

    private fun isTwoFileDifferent(originalFile: Path, newFile: Path): Boolean {
        if (Files.size(originalFile) != Files.size(newFile)) {
            return false
        }
        val originalBytes = Files.readAllBytes(originalFile)
        val newFileBytes = Files.readAllBytes(newFile)

        return !originalBytes.contentEquals(newFileBytes)
    }

    private fun saveToLocalFile(prefix: String, suffix: String, gsReference: StorageReference) {
        try {
            if (labelFileSignLanguage == null ||
                labelFileObjectDetection == null ||
                labelFileCurrencyDetection == null
            ) {
                val tempFile = File.createTempFile(prefix, suffix)

                gsReference.getFile(tempFile)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val targetPath = StringBuilder().apply {
                                append(ConstVal.ABSOLUTE_PATH)
                                append(prefix)
                                append(suffix)
                            }.toString()
                            val originalFile = saveToPermanentFile(targetPath, tempFile)
                            successListener(prefix, originalFile.path)
                        }
                    }
                    .addOnFailureListener {
                        // Handle any Errors
                        failureListener()
                    }

                if (labelFileObjectDetection != null &&
                    labelFileCurrencyDetection != null && labelFileSignLanguage != null
                ) clearCacheLabels()
            } else if (
                labelFileSignLanguage != null &&
                labelFileCurrencyDetection != null &&
                labelFileObjectDetection != null
            ) {
                // Check if there is a two file
                val tempFile = File.createTempFile(prefix, suffix)
                gsReference.getFile(tempFile)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val targetPath = StringBuilder().apply {
                                append(ConstVal.ABSOLUTE_PATH)
                                append(prefix)
                                append(suffix)
                            }.toString()

                            when (prefix) {
                                PREFIX_CD -> {
                                    val originalFileCurrencyDetection =
                                        File(labelFileCurrencyDetection as String)
                                    if (isTwoFileDifferent(originalFileCurrencyDetection.toPath(),
                                            tempFile.toPath())
                                    ) {
                                        Log.d("DEBUGTAGS", "CurrencyDifferent")
                                        val originalFile = saveToPermanentFile(targetPath, tempFile)
                                        successListener(prefix, originalFile.path)
                                    }
                                }

                                PREFIX_OD -> {
                                    val originalFileObjectDetection =
                                        File(labelFileObjectDetection as String)

                                    if (isTwoFileDifferent(originalFileObjectDetection.toPath(),
                                            tempFile.toPath())
                                    ) {
                                        Log.d("DEBUGTAGS", "ObjectDetectionDifferent")
                                        val originalFile = saveToPermanentFile(targetPath, tempFile)
                                        successListener(prefix, originalFile.path)
                                    }
                                }

                                PREFIX_SL -> {
                                    val originalFileSignLanguage =
                                        File(labelFileSignLanguage as String)

                                    if (isTwoFileDifferent(originalFileSignLanguage.toPath(),
                                            tempFile.toPath())
                                    ) {
                                        Log.d("DEBUGTAGS", "SignLanguageDifferent")
                                        val originalFile = saveToPermanentFile(targetPath, tempFile)
                                        successListener(prefix, originalFile.path)
                                    }

                                }

                            }

                        }
                    }
                    .addOnFailureListener {
                        // nanti ini dimasukin failure
                        failureListener()
                    }

            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun successListener(prefix: String, path: String) {
        when (prefix) {
            PREFIX_CD -> {
                labelFileCurrencyDetection = path
            }
            PREFIX_OD -> {
                labelFileObjectDetection = path
            }
            PREFIX_SL -> {
                labelFileSignLanguage = path
            }
        }

        updateObserverSuccess()
    }

    private fun failureListener() {
        clearCacheLabels()
        clearTheLabelFile()
        clearTheFiles()
        updateObserverFailure()
    }

    private fun saveToPermanentFile(fileName: String, tempFile: File): File {

        Log.d("DEBUGTAGS", tempFile.path)
        val originalFile: File = File(fileName)

        val src: FileChannel = FileInputStream(tempFile).channel
        val dst: FileChannel = FileOutputStream(originalFile).channel

        dst.transferFrom(src, 0, src.size())
        return originalFile
    }

    private fun getTheObjectDetectionFile() {
        try {
            val gsReferenceObjectDetectionFile = storage.getReferenceFromUrl(
                StringBuilder().apply {
                    append(
                        BuildConfig.OBJECT_DETECTION_STORAGE_PATH
                    )
                }.toString())
            saveToLocalFile(PREFIX_OD, SUFFIX_ALL, gsReferenceObjectDetectionFile)
        }  catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTheCurrencyDetectionFile() {
        try {
            val gsReferenceCurrencyDetectionFile = storage.getReferenceFromUrl(StringBuilder().apply {
                append(
                    BuildConfig.CURRENCY_DETECTION_STORAGE_PATH
                )
            }.toString())
            saveToLocalFile(PREFIX_CD, SUFFIX_ALL, gsReferenceCurrencyDetectionFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTheSignLanguageFile() {
        try {
            val gsReferenceSignLanguageFile = storage.getReferenceFromUrl(StringBuilder().apply {
                append(
                    BuildConfig.SIGN_LANGUAGE_CLOUD_STORAGE_PATH
                )
            }.toString())

            saveToLocalFile(PREFIX_SL, SUFFIX_ALL, gsReferenceSignLanguageFile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun clearCacheLabels() {
        // Clear the cache
        val pathToDelete: File = File(StringBuilder().apply {
            append(ConstVal.ABSOLUTE_PATH)
            append("/cache/")
        }.toString())

        if (pathToDelete.isDirectory) {
            pathToDelete.listFiles()?.forEach {
                if (isEndWithTxt(it.path)) {
                    it.delete()
                }
            }
        }
    }

    private fun clearTheFiles() {
        labelFileCurrencyDetection = null
        labelFileSignLanguage = null
        labelFileObjectDetection = null
    }

    private fun clearTheLabelFile() {
        // Cleat the label File
        val pathToDelete: File = File(ConstVal.ABSOLUTE_PATH)
        pathToDelete.listFiles()?.forEach {
            if (isEndWithTxt(it.path)) {
                it.delete()
            }
        }
    }

    override fun registerObserver(o: CloudStorageObserver) {
        if (!observers.contains(o)) observers.add(o)
    }

    override fun removeObserver(o: CloudStorageObserver) {
        if (observers.contains(o)) observers.remove(o)
    }

    override fun updateObserverSuccess() {
        for (observer: CloudStorageObserver in observers) {
            observer.updateObserverCloudStorageSuccess()
        }
    }

    override fun updateObserverFailure() {
        for (observer: CloudStorageObserver in observers) {
            observer.updateObserverCloudStorageFailure()
        }
    }
}