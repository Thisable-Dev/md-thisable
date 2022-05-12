package com.bintangpoetra.thisable.presentation.currency_detection

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.analyzers.CurrencyAnalyzer
import com.bintangpoetra.thisable.data.dummy.ServeListQuestions
import com.bintangpoetra.thisable.databinding.ActivityCurrencyBinding
import com.bintangpoetra.thisable.interfaces.ObjectOptionInterface
import com.bintangpoetra.thisable.utils.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CurrencyActivity : AppCompatActivity() {
    private val context = this
    private lateinit var binding: ActivityCurrencyBinding
    private lateinit var cameraExecutor : ExecutorService
    private lateinit var objAnalyzer : CurrencyAnalyzer

    private fun init () {
        cameraExecutor = Executors.newSingleThreadExecutor()
        objAnalyzer = CurrencyAnalyzer(binding.graphicOverlay, this)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        val itemListener = object : ObjectOptionInterface {
            override fun onClick(data: String) {
                when(data) {
                    getString(R.string.question_1_currency_detection) -> {
                        showToastMessage(context, data + " Pressed")

                    }
                    getString(R.string.question_2_currency_detection) -> {
                        showToastMessage(context, data + " Pressed")
                    }
                }
            }

            override fun onLongClickListener(data: String) {
                when(data) {
                    getString(R.string.question_1_currency_detection) -> {
                        var items = objAnalyzer.getCurrencyDetected()
                        if (!items.isEmpty()) {
                            val returned = makeItOneString(countTheObj(items))
                            showToastMessage(this@CurrencyActivity, getString(R.string.response_1_currency_detection, returned, sumTheDetectedCurrency(items).toString()))
                        }
                    }
                    getString(R.string.question_2_currency_detection) -> {
                        var items = objAnalyzer.getCurrencyDetected()
                        val returnedText = makeItOneString(countTheObj(items))
                        showToastMessage(context, getString(R.string.response_2_currency_detection, sumTheDetectedCurrency(items).toString()))
                    }
                }
            }

        }
        binding.viewFinder.setOnLongClickListener{
            showAlertDialogObjDetection(this, ServeListQuestions.getListQuestionCurrency(this@CurrencyActivity),itemListener )
            true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_CODE) {
            if(allPermissionGranted()) {
                startCamera()
            }
            else {
                showToastMessage(this@CurrencyActivity, "Permission not Granted")
                finish()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        if (allPermissionGranted()) {
            startCamera()
        }
        else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, PERMISSION_CODE)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val runnableInterface = Runnable {
            val cameraProvider : ProcessCameraProvider = cameraProviderFuture.get()
            val preview : Preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, objAnalyzer)
                    }
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
        cameraProviderFuture.addListener(runnableInterface, ContextCompat.getMainExecutor(context))
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    private fun allPermissionGranted() : Boolean {
        return REQUIRED_PERMISSION.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private val PERMISSION_CODE : Int = 10
        private val TAG : String = CurrencyActivity::class.java.simpleName
    }
}