package com.devtedi.tedi.presentation.feature_cloud

import com.devtedi.tedi.BuildConfig
import com.devtedi.tedi.interfaces.observer_cloudstorage.CloudStorageObserver
import com.devtedi.tedi.interfaces.observer_cloudstorage.CloudStorageSubject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.lang.StringBuilder
import java.lang.reflect.Array

object CloudStorage : CloudStorageSubject {

    private const val objectDetectionLabelName : String = "/label_obj_detection.txt"
    private const val signLanguageLabelName : String = "/label_sign_language.txt"
    private const val currencyDetectionLabelName : String ="/label_currency.txt"
    private const val SUFFIX : String = "labels"
    private val storage  : FirebaseStorage = Firebase.storage(BuildConfig.FIREBASE_STORAGE_PATH)

    private val observers : ArrayList<CloudStorageObserver> = ArrayList()

    fun downloadLabelFiles()
    {
        getTheObjectDetectionFile()
        getTheCurrencyDetectionFile()
        getTheSignLanguageFile()
    }

    private fun getTheObjectDetectionFile()
    {
        val gsReferenceObjectDetectionFile = storage.getReference(StringBuilder().apply {
            append(BuildConfig.FIREBASE_STORAGE_PATH)
            append(objectDetectionLabelName)
        }.toString())

        saveToLocalFile(SUFFIX, "txt", gsReferenceObjectDetectionFile)
    }

    private fun saveToLocalFile(suffix : String, prefix : String, gsReference : StorageReference)
    {
        val localFile = File.createTempFile(suffix, prefix)
        gsReference.getFile(localFile)
            .addOnSuccessListener {
                // LocalTempFileHasbeencreated
                it.toString()
                successListener()
            }
            .addOnFailureListener{
                // Handle any Errors
                failureListener()
            }
    }

    private fun successListener()
    {
        updateObserverSuccess()
    }

    private fun failureListener()
    {
        updateObserverFailure()
    }

    private fun getTheCurrencyDetectionFile()
    {
        val gsReferenceCurrencyDetectionFile = storage.getReference(StringBuilder().apply {
            append(BuildConfig.FIREBASE_STORAGE_PATH)
            append(currencyDetectionLabelName)
        }.toString())
        saveToLocalFile(suffix = SUFFIX , prefix = "txt", gsReferenceCurrencyDetectionFile)

    }

    private fun getTheSignLanguageFile()
    {
        val gsReferenceSignLanguageFile = storage.getReference(StringBuilder().apply {
            append(BuildConfig.FIREBASE_STORAGE_PATH)
            append(signLanguageLabelName)
        }.toString())

        saveToLocalFile(SUFFIX, "txt", gsReferenceSignLanguageFile)
    }

    override fun registerObserver(o: CloudStorageObserver) {
     if (!observers.contains(o)) observers.add(o)
    }

    override fun removeObserver(o: CloudStorageObserver) {
        if(observers.contains(o)) observers.remove(o)
    }

    override fun updateObserverSuccess() {
        for(observer : CloudStorageObserver in observers)
        {
            observer.updateObserverCloudStorageSuccess()
        }
    }

    override fun updateObserverFailure() {
        for(observer : CloudStorageObserver in observers)
        {
            observer.updateObserverCloudStorageFailure()
        }
    }


}