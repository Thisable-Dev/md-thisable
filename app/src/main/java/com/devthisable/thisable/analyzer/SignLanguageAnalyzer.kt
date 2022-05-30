package com.devthisable.thisable.analyzer

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devthisable.thisable.interfaces.SignLanguageListener
import com.devthisable.thisable.utils.GraphicOverlay
import com.devthisable.thisable.utils.ObjectGraphic
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import kotlinx.coroutines.*

class SignLanguageAnalyzer(private val graphicOverlay: GraphicOverlay, private val context : Context,) : ImageAnalysis.Analyzer {

    private val localModel = LocalModel.Builder()
        .setAssetFilePath("ssd.tflite")
        .build()
    private lateinit var subscribeSignLanguageListener: SignLanguageListener
    private var GLOBAL_TRACKING_ID : Int ?= null
    private val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .setClassificationConfidenceThreshold(0.5F)
        .build()

    fun setSignLanguageListener(signLanguageListener: SignLanguageListener) {
        this.subscribeSignLanguageListener = signLanguageListener
    }
    private val outputData = arrayOfNulls<String>(1)
    private val objectDetector : ObjectDetector = ObjectDetection.getClient(options)
    private val overlay = graphicOverlay
    private val lens_facing = CameraSelector.LENS_FACING_BACK

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
                // Because only One
                if(detectedObject.trackingId != GLOBAL_TRACKING_ID) {
                    subscribeSignLanguageListener.onChangedPose(detectedObject.labels[0].text)
                    GLOBAL_TRACKING_ID = detectedObject.trackingId
                }
            }
            overlay.postInvalidate()
        }
    }


    fun getOutputData() :Array<String>{
        return outputData.requireNoNulls()
    }


}
