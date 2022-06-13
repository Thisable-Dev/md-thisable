package com.devthisable.thisable.analyzer

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devthisable.thisable.interfaces.SignLanguageListener
import com.devthisable.thisable.interfaces.SignlanguageContentListener
import com.devthisable.thisable.presentation.feature_sign_language.SignLanguageFragment
import com.devthisable.thisable.utils.GraphicOverlay
import com.devthisable.thisable.utils.ObjectGraphic
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions

class SignLanguageAnalyzer(private val graphicOverlay: GraphicOverlay, private val context: Context) : ImageAnalysis.Analyzer {


    private lateinit var subscribeSignLanguageListener: SignLanguageListener
    private var subscribeSignLanguageContentListener : SignlanguageContentListener
    private var GLOBAL_TRACKING_ID : Int? = null
    private var globalKeyboardState : Boolean = false

    private val localModel = LocalModel.Builder()
        .setAssetFilePath("final_SignLanguage_ack_hpc.tflite")
        .build()
    private val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableClassification()
        .setClassificationConfidenceThreshold(LABEL_CONFIDENCE)
        .build()

    fun setSignLanguageListener(signLanguageListener: SignLanguageListener) {
        this.subscribeSignLanguageListener = signLanguageListener
    }

    private val objectDetector : ObjectDetector = ObjectDetection.getClient(options)
    private val overlay = graphicOverlay
    private val lens_facing = CameraSelector.LENS_FACING_BACK


    init {
        subscribeSignLanguageContentListener = object : SignlanguageContentListener {
            override fun onListenContent(state: Boolean) {
                globalKeyboardState = state
            }
        }

        SignLanguageFragment.setSignLanguageContentListener(subscribeSignLanguageContentListener)


    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {

        val isImageFlipped= lens_facing == CameraSelector.LENS_FACING_FRONT
        val rotationDegress = image.imageInfo.rotationDegrees
        if (rotationDegress == 180 || rotationDegress == 0) overlay.setImageSourceInfo(image.image!!.width, image.image!!.height, isImageFlipped )
        else overlay.setImageSourceInfo(image.image!!.height, image.image!!.width, isImageFlipped)
        try {
            val frame = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
            objectDetector.process(frame).addOnSuccessListener {
                sucessListener(it)
            }
                .addOnFailureListener {
                    it.printStackTrace()
                }
                .addOnCompleteListener {
                    image.close()

                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Public Fun
    private fun sucessListener(detectedObjects:MutableList<DetectedObject>) {
        overlay.clear()
        if (!globalKeyboardState) {
            for(detectedObject in detectedObjects) {
                if (detectedObject.labels.isNotEmpty()) {
                    val objGraphic = ObjectGraphic(this.graphicOverlay, detectedObject)
                    overlay.add(objGraphic)
                    // Because only One
                    if (detectedObject.trackingId != GLOBAL_TRACKING_ID) {
                        subscribeSignLanguageListener.onChangedPose(detectedObject.labels[0].text)
                        GLOBAL_TRACKING_ID = detectedObject.trackingId
                    }
                }
            }
        }

        overlay.postInvalidate()
    }

    companion object {
        const val LABEL_CONFIDENCE : Float = 0.8F
        const val HEIGHT : Int = 470
        const val WIDTH : Int = 372
    }

}
