package com.devthisable.thisable.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devthisable.thisable.utils.GraphicOverlay
import com.devthisable.thisable.utils.ObjectGraphic
import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.linkfirebase.FirebaseModelSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import kotlinx.coroutines.*


class ObjectAnalyzer(private val graphicOverlay: GraphicOverlay, private val context : Context) : ImageAnalysis.Analyzer {

    // For Unique Labels
    private var setOfLabels = mutableSetOf<String>()
    private var mapOfLabels = mutableMapOf<String, Int >()
    private var bunchOfLabelsCounted = mutableListOf<String>()
    private lateinit var localModel : LocalModel
    private lateinit var remoteModel : CustomRemoteModel
    private lateinit var optionsRemote : CustomObjectDetectorOptions
    init {
        mapOfLabels["monitor"] = 0

        localModel =  LocalModel.Builder()
            .setAssetFilePath("ssd.tflite")
            .build()

        remoteModel = CustomRemoteModel.Builder(
            FirebaseModelSource.Builder("ssd.tflite").build()).build()

        val downloadConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        RemoteModelManager.getInstance().isModelDownloaded(remoteModel).addOnSuccessListener { isDownloaded ->
            lateinit var optionsRemoteBuilder : CustomObjectDetectorOptions.Builder
            if (isDownloaded)
            {
                optionsRemoteBuilder = CustomObjectDetectorOptions.Builder(remoteModel)
            }
            else {
                optionsRemoteBuilder = CustomObjectDetectorOptions.Builder(localModel)
                RemoteModelManager.getInstance().download(remoteModel, downloadConditions)
                    .addOnSuccessListener {
                        // TodoCreate Some Interface to Provide data
                    }
                    .addOnFailureListener { e->
                        Log.d("TOASTED", e.message.toString())
                    }
                optionsRemote = optionsRemoteBuilder.enableClassification()
                    .enableClassification()
                    .setClassificationConfidenceThreshold(0.1f)
                    .build()
            }

        }

    }


    val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .setClassificationConfidenceThreshold(0.3f)
        .build()

    val objectDetector = ObjectDetection.getClient(options)
    // Overlay means the drawing that we would like to use
    val overlay = graphicOverlay
    private val lensFacing = CameraSelector.LENS_FACING_BACK // Back camera

    private fun clearTheSetEveryNTime() {
        CoroutineScope(Dispatchers.Main).launch {
            val queue = async(Dispatchers.IO) {
                delay(100)
                setOfLabels = mutableSetOf<String>()
                bunchOfLabelsCounted = mutableListOf()
                mapOfLabels["monitor"] = 0
            }
            queue.await()
        }
    }

    fun getCurItemCounter () : List<String> {
        return bunchOfLabelsCounted
    }
    fun getCurrlist() : List<String> {
        return setOfLabels.toList()
    }



    private fun playTheSound(label : String ) {
        /*
        if(label == "monitor") {
            val implementation = onImplementation(label)
            if (implementation != - 1) {
                mediaPlayer.load(implementation)
                mediaPlayer.close()
            }
        }
         */
    }
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT // Kalau misalnya kamera depan, maka ada kemungkinan dia bakalan flipped ketika dilakukan inference
        val rotationDegress = image.imageInfo.rotationDegrees

        // Rotation Degress ini kegenerate ketika Gunakan Lens Facing front, kalau back pake yang else
        if (rotationDegress == 0 || rotationDegress == 180) overlay.setImageSourceInfo(image.width, image.height, isImageFlipped)
        else overlay.setImageSourceInfo(image.height, image.width, isImageFlipped)

        val frame = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
        objectDetector.process(frame).addOnSuccessListener { detectedObjects ->
            overlay.clear()
            for (detectedObject in detectedObjects) {
                if (!detectedObject.labels.isEmpty()) {
                    val objGraphic = ObjectGraphic(this.overlay, detectedObject)
                    this.overlay.add(objGraphic)
                    for (label in detectedObject.labels) {
                        bunchOfLabelsCounted.add(label.text)
                        setOfLabels.add(label.text)
                    }
                    for (label in setOfLabels) {
                        if(mapOfLabels[label] == 0) {
                            Log.d("ENDGAME ",label + mapOfLabels[label].toString())
                            // playTheSound(label)
                            mapOfLabels[label] = 1
                        }
                    }
                    clearTheSetEveryNTime()
                }
            }
            this.overlay.postInvalidate()
        }.addOnFailureListener { e->
            e.printStackTrace()
        }.addOnCompleteListener {
            image.close()
        }
    }
}
