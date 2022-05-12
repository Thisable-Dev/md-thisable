package com.bintangpoetra.thisable.presentation.obj_detection

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.adapters.ObjectOptionAdapter
import com.bintangpoetra.thisable.analyzers.ObjectAnalyzer
import com.bintangpoetra.thisable.data.dummy.ServeListQuestions
import com.bintangpoetra.thisable.interfaces.ObjectOptionInterface
import com.bintangpoetra.thisable.utils.GraphicOverlay
import com.bintangpoetra.thisable.utils.countTheObj
import com.bintangpoetra.thisable.utils.makeItOneString
import com.bintangpoetra.thisable.utils.showToastMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ObjDetectionActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    //private lateinit var camera_capture_button: Button
    private lateinit var viewFinder: PreviewView
    private lateinit var graphicOverlay: GraphicOverlay
    private lateinit var iv_volume : ImageView
    private var isSoundOn : Boolean = false
    private var stateCamera : Boolean = false
    private var soundIsPlaying : Boolean = false
    private lateinit var objAnalyzer : ObjectAnalyzer
    private var vCounterPush = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera(false)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        iv_volume =findViewById(R.id.iv_volume)
        graphicOverlay = findViewById(R.id.graphicOverlay)
        viewFinder = findViewById(R.id.viewFinder)
        objAnalyzer = ObjectAnalyzer(graphicOverlay, this)
        viewFinder.setOnLongClickListener{
            stateCamera = true
            showAlertDialog()
            true
        }
       /* iv_volume.setOnClickListener {
            if (isSoundOn) {
                iv_volume.setImageDrawable(getDrawable(R.drawable.ic_action_volume_off))
                isSoundOn = false
                startCamera(false)
            }
            else {
                iv_volume.setImageDrawable(getDrawable(R.drawable.ic_action_volume_on))
                isSoundOn = true
                startCamera(true)
            }
        }
            */
    }

    private fun getListOfRaw() : List<Int> {
        return arrayListOf(2,3)
        //return arrayListOf(R.raw.v1, R.raw.v2, R.raw.v3, R.raw.v4)
    }

    /*
    private fun playSoundInInterval(interval : Long, listOfRawVoiceAudio : List<Int> ) {
        val mediaPlayerGenerator = MediaPlayerGenerator(this@MainActivity)
        val countDownInteval: Long = 2000
        var indexOfRawVoice = 0
        val countDownTimer = object : CountDownTimer(interval , countDownInteval) {
            override fun onTick(timer: Long) {
                var currentTimer = timer / 100  + 1
                Log.d("HELLO",currentTimer.toString())
                if(currentTimer.toInt() % 10 == 0) {
                    try {
                        mediaPlayerGenerator.load(listOfRawVoiceAudio[indexOfRawVoice])
                        indexOfRawVoice += 1
                    }
                    catch(e : IndexOutOfBoundsException) {
                        return
                    }
                }
                if(mediaPlayerGenerator.isPlaying() == true) {
                    mediaPlayerGenerator.close()
                }
            }
            override fun onFinish() {
                mediaPlayerGenerator.close()
            }
        }
        countDownTimer.start()
    }
    */

    private fun showAlertDialog() {
        val alertDialog = Dialog(this)
        val tempArrayList = ServeListQuestions.getListQuestion(this)
        val adapter = ObjectOptionAdapter(tempArrayList)
        val subscriberItemListener : ObjectOptionInterface = object : ObjectOptionInterface {
            override fun onClick(data: String) {
                if (data == this@ObjDetectionActivity.getString(R.string.question_1_obj_detection)) {
                    showToastMessage(this@ObjDetectionActivity, "Ketuk dan Tahan Untuk mengetahui ada apa saja didepan anda")
                }
            }
            override fun onLongClickListener(data: String) {
                when (data) {
                    this@ObjDetectionActivity.getString(R.string.question_1_obj_detection) -> {
                        var itemConfig = objAnalyzer.getCurItemCounter()
                        if (itemConfig.isEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(100)
                                itemConfig = objAnalyzer.getCurItemCounter()
                                if (!itemConfig.isEmpty() && itemConfig.size <= 10) {
                                    val interval = (itemConfig.size * 2000) // + 2000 if Necessary
                                    val stringReturned = makeItOneString(countTheObj(itemConfig))
                                    showToastMessage(this@ObjDetectionActivity, this@ObjDetectionActivity.getString(R.string.response_1_obj_detection, stringReturned.dropLast(1)))
                                    //playSoundInInterval(interval.toLong(), getListOfRaw())
                                } else {
                                    showToastMessage(
                                        this@ObjDetectionActivity,
                                        "Tidak ada yang terdeteksi")
                                }
                            }
                        } else {
                            if(itemConfig.size <= 10) {
                                val interval = (itemConfig.size * 2000) // + 2000 if Necessary
                                val stringReturned = makeItOneString(countTheObj(itemConfig))
                                    showToastMessage(this@ObjDetectionActivity,
                                    this@ObjDetectionActivity.getString(
                                        R.string.response_1_obj_detection,
                                        stringReturned.dropLast(1))
                                    )
                                //playSoundInInterval(interval.toLong(), getListOfRaw())
                            }
                        }
                    }
                    // Response 2
                    // Response 3
                }
            }
        }
        alertDialog.setContentView(R.layout.custom_dialog)
        adapter.setOnClickItemListener(subscriberItemListener)
        val rvDialog = alertDialog.findViewById(R.id.dialog_rv) as RecyclerView
        rvDialog.layoutManager = LinearLayoutManager(this)
        rvDialog.adapter = adapter
        alertDialog.show()
    }
    private fun startCamera(stateSound : Boolean ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview : Preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, objAnalyzer)
                    }
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "Live Object Detector Sample App"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(false )
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}