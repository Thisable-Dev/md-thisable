package com.devthisable.thisable.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devthisable.thisable.interfaces.FeedbackListener
import com.devthisable.thisable.presentation.feature_currency.CurrencyFragment
import com.devthisable.thisable.utils.GraphicOverlay
import com.devthisable.thisable.utils.ObjectGraphic
import com.devthisable.thisable.utils.SoundPlayer
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import kotlinx.coroutines.*


class CurrencyAnalyzer(private val graphicOverlay: GraphicOverlay, private val context : Context,) : ImageAnalysis.Analyzer {

    private var oneFrameDatabase = mutableListOf<String>()
    private var allObjectDetectedDatabase = mutableListOf<String>()
    private var soundPlayer = SoundPlayer(context)
    private var GLOBAL_SOUND : Boolean  = false
    private lateinit var subscribeFeedbackListener: FeedbackListener
    private lateinit var objectDetector : ObjectDetector
    private val overlay = graphicOverlay
    private val lens_facing = CameraSelector.LENS_FACING_BACK
    private var bunchCurrencyDetected : MutableList<String> = mutableListOf()

    init {
        var subscribeFeedbackListener: FeedbackListener = object : FeedbackListener {
            override fun onListenFeedback(stateSound: Boolean) {
                GLOBAL_SOUND = stateSound
            }
        }
        val localModel = LocalModel.Builder()
            .setAssetFilePath("finalModel_currency.tflite")
            .build()

        val options = CustomObjectDetectorOptions.Builder(localModel)
            .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .setClassificationConfidenceThreshold(CONFIDENCE_SCORE)
            .build()
        objectDetector = ObjectDetection.getClient(options)
        CurrencyFragment.setOnFeedbackListener(subscribeFeedbackListener)
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val isImageFlipped= lens_facing == CameraSelector.LENS_FACING_FRONT
        val rotationDegress = image.imageInfo.rotationDegrees
        if (rotationDegress == 180 || rotationDegress == 0) overlay.setImageSourceInfo(image.width, image.height, isImageFlipped )
        else overlay.setImageSourceInfo(image.height, image.width, isImageFlipped)

        val frame = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
        objectDetector.process(frame).addOnSuccessListener {
            sucessListener(it)
        }
            .addOnFailureListener{
                it.printStackTrace()
            }
            .addOnCompleteListener{
                image.close()
            }
    }
    // Public Fun
    private fun sucessListener(detectedObjects:MutableList<DetectedObject>) {
        overlay.clear()
        for(detectedObject in detectedObjects) {
            if(!detectedObject.labels.isEmpty()) {
                val objGraphic = ObjectGraphic(this.graphicOverlay, detectedObject)
                overlay.add(objGraphic)
                for (label in detectedObject.labels) {
                    bunchCurrencyDetected.add(label.text)
                     if (label.confidence > CONFIDENCE_SCORE) {
                         oneFrameDatabase.add(label.text)
                     }
                }
                clearTheSetEveryNTime()
            }
            if(GLOBAL_SOUND) {
                checkIfSoundGoingToPlay()
            }
            overlay.postInvalidate()
        }
    }

    private fun clearTheSetEveryNTime() {
        CoroutineScope(Dispatchers.Main).launch {
            val queue = async(Dispatchers.IO) {
                delay(100)
                bunchCurrencyDetected = mutableListOf()
            }
            queue.await()
        }
    }

    fun getCurrencyDetected() : List<String> {
        return bunchCurrencyDetected
    }


    private fun checkIfSoundGoingToPlay() {
        var sentences  = mutableListOf<String>()
        if (oneFrameDatabase.isNotEmpty()) {
            for (label in oneFrameDatabase) {
                // Langsung Check di full databasenya
                if (!allObjectDetectedDatabase.contains(label)) {
                    sentences.add(label)
                    allObjectDetectedDatabase.add(label)
                }
            }

            try {
                if (sentences.isNotEmpty()) {
                    // Play the sound here
                    CoroutineScope(Dispatchers.Main).launch {
                        for (label in sentences) {
                            soundPlayer.playSound(label)
                            delay(200)
                        }
                    }
                    // playTheSound(sentences.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                oneFrameDatabase.clear()
            }
            try {
                val list =   allObjectDetectedDatabase.slice(IntRange(allObjectDetectedDatabase.size -3 ,allObjectDetectedDatabase.size -1 ))
                allObjectDetectedDatabase = list.toMutableList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val CONFIDENCE_SCORE = 0.3f
    }
}
