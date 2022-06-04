package com.devthisable.thisable.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devthisable.thisable.R
import com.devthisable.thisable.interfaces.FeedbackListener
import com.devthisable.thisable.presentation.feature_object.ObjectDetectionFragment
import com.devthisable.thisable.utils.GraphicOverlay
import com.devthisable.thisable.utils.ObjectGraphic
import com.devthisable.thisable.utils.SoundPlayer
import com.devthisable.thisable.utils.image_utility.YuvToRgbConverter
import com.devthisable.thisable.utils.showToastMessage
import com.google.mlkit.common.model.CustomRemoteModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.LocalModel
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.linkfirebase.FirebaseModelSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions
import kotlinx.coroutines.*
import kotlin.collections.HashMap
import kotlin.text.StringBuilder


class ObjectAnalyzer(private val graphicOverlay: GraphicOverlay, private val context : Context) : ImageAnalysis.Analyzer {

    // For Unique Labels
    private var setOfLabels = mutableSetOf<String>()
    private var mapOfLabels = mutableMapOf<String, Int >()
    private var bunchOfLabelsCounted = mutableListOf<String>()
    private lateinit var subscribeFeedbackListener: FeedbackListener
    private var soundPlayer = SoundPlayer(context)
    private var globalStateSound : Boolean  = false
    private var globalStateKeyboard : Boolean = false
    private var one_frame_database = mutableListOf<String>()
    private var all_object_detected_database = mutableListOf<String>()


    private val hashMap = HashMap<String, String>()
    private lateinit var localModel : LocalModel
    private lateinit var remoteModel : CustomRemoteModel
    private lateinit var optionsRemote : CustomObjectDetectorOptions


    init {
        hashMap["fruit"] = context.resources.getString(R.string.raw_sound_buah)
        hashMap["motorbike"] = context.resources.getString(R.string.raw_sound_motor)
        hashMap["cat"] = context.resources.getString(R.string.raw_sound_kucing)
        hashMap["dog"] = context.resources.getString(R.string.raw_sound_anjing)
        hashMap["flower"] = context.resources.getString(R.string.raw_sound_bunga)

        localModel =  LocalModel.Builder()
            .setAssetFilePath("final_objectDetection.tflite")
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
                    .enableMultipleObjects()
                    .setClassificationConfidenceThreshold(0.5f)
                    .build()
            }

        }

        subscribeFeedbackListener = object : FeedbackListener {
            override fun onListenFeedback(stateSound: Boolean) {
                    globalStateSound = stateSound
            }
        }
        ObjectDetectionFragment.setFeedbackListener(subscribeFeedbackListener)
    }

    val options = CustomObjectDetectorOptions.Builder(localModel)
        .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
        .enableMultipleObjects()
        .enableClassification()
        .setClassificationConfidenceThreshold(LABEL_CONFIDENCE)
        .build()


    var tempImage = Bitmap.createBitmap(640, 480,Bitmap.Config.ARGB_8888 )
    val objectDetector = ObjectDetection.getClient(options)
    // Overlay means the drawing that we would like to use
    val overlay = graphicOverlay
    private val lensFacing = CameraSelector.LENS_FACING_BACK // Back camera

    private fun clearTheSetEveryNTime() {
        CoroutineScope(Dispatchers.Main).launch {
            val queue = async(Dispatchers.IO) {
                delay(100)
                bunchOfLabelsCounted = mutableListOf()
            }
            queue.await()
        }
    }



    fun getCurItemCounter () : List<String> {
        return bunchOfLabelsCounted
    }

    fun mappedLabel(label : String) : String?{

        return hashMap[label.filter { !it.isWhitespace() }]
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT // Kalau misalnya kamera depan, maka ada kemungkinan dia bakalan flipped ketika dilakukan inference
        val rotationDegress = image.imageInfo.rotationDegrees

        // Rotation Degress ini kegenerate ketika Gunakan Lens Facing front, kalau back pake yang else
        if (rotationDegress == 0 || rotationDegress == 180) overlay.setImageSourceInfo(image.width, image.height, isImageFlipped)
        else overlay.setImageSourceInfo(image.height, image.width, isImageFlipped)
        YuvToRgbConverter(context).yuvToRgb(image.image!!, tempImage)
        tempImage = Bitmap.createScaledBitmap(tempImage, WIDTH, HEIGHT, false)
        val frame = InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees)
        objectDetector.process(frame).addOnSuccessListener { detectedObjects ->
            overlay.clear()
            for (detectedObject in detectedObjects) {
                if (!detectedObject.labels.isEmpty()) {
                    val objGraphic = ObjectGraphic(this.overlay, detectedObject)
                    this.overlay.add(objGraphic)
                    for (label in detectedObject.labels) {
                        val mapped = mappedLabel(label.text)
                        if (mapped != null) {
                            bunchOfLabelsCounted.add(mapped)
                            setOfLabels.add(label.text)
                        }
                        if(label.confidence > LABEL_CONFIDENCE) {
                            if (mapped != null) {
                                one_frame_database.add(mapped)
                            }
                        }
                    }
                    clearTheSetEveryNTime()
                }
            }
            if(globalStateSound) {

                checkIfSoundGoingToPlay()
            }
            this.overlay.postInvalidate()
        }.addOnFailureListener { e->
            e.printStackTrace()
        }.addOnCompleteListener {
            image.close()
        }
    }

    private fun checkIfSoundGoingToPlay() {
        var sentences  = mutableListOf<String>()
        if (one_frame_database.isNotEmpty()) {
            for (label in one_frame_database) {
                // Langsung Check di full databasenya
                if (!all_object_detected_database.contains(label)) {
                    sentences.add(label)
                    all_object_detected_database.add(label)
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                one_frame_database.clear()
            }
            try {
                val list =   all_object_detected_database.slice(IntRange(all_object_detected_database.size ,all_object_detected_database.size -1 ))
                all_object_detected_database = list.toMutableList()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val LABEL_CONFIDENCE = 0.8f
        private const val WIDTH = 224
        private const val HEIGHT = 224

    }
}
