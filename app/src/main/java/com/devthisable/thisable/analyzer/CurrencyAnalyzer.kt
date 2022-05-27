package com.devthisable.thisable.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devthisable.thisable.utils.GraphicOverlay
import com.devthisable.thisable.utils.ObjectGraphic
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import kotlinx.coroutines.*


class CurrencyAnalyzer(private val graphicOverlay: GraphicOverlay, private val context : Context,) : ImageAnalysis.Analyzer {

    private val localModel = LocalModel.Builder()
        .setAssetFilePath("ssd.tflite")
        .build()

    private val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .setClassificationConfidenceThreshold(0.1F)
        .build()

    private val objectDetector : ObjectDetector = ObjectDetection.getClient(options)
    private val overlay = graphicOverlay
    private val lens_facing = CameraSelector.LENS_FACING_BACK
    private var bunchCurrencyDetected : MutableList<String> = mutableListOf()


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
            Log.d("YOMAN",detectedObject.labels.toString())
            if(!detectedObject.labels.isEmpty()) {
                val objGraphic = ObjectGraphic(this.graphicOverlay, detectedObject)
                overlay.add(objGraphic)
                for (label in detectedObject.labels) {
                    Log.d("LabEL",label.text.toString())
                    bunchCurrencyDetected.add(label.text)
                }
                clearTheSetEveryNTime()
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

}
