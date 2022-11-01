package com.devtedi.tedi.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import timber.log.Timber
import java.util.concurrent.ExecutionException

class CameraProcess {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>;
    private  var cameraProvider : ProcessCameraProvider? = null

    fun allPermissionGranted(context: Context): Boolean {
        for (permission in ConstVal.arrayOfPermissions) {
            if (ContextCompat.checkSelfPermission(context,
                    permission) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun requestPermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, ConstVal.arrayOfPermissions,
            REQUEST_CODE_PERMISSION
        )
    }

    fun startCamera(
        context: Context,
        analyzer: ImageAnalysis.Analyzer,
        previewView: PreviewView,
    ) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context), analyzer)

                val previewBuilder: Preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build()

                val cameraSelector: CameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                previewBuilder.setSurfaceProvider(previewView.surfaceProvider)
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(context as LifecycleOwner,
                    cameraSelector,
                    imageAnalysis,
                    previewBuilder)

            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }
    fun destroy()
    {
        Log.d("TAGS", "onDestroyCameraProcess")
        cameraProvider?.unbindAll()
        cameraProvider =null
    }


    fun showCameraSupportSize(activity: Activity) {
        val manager: CameraManager =
            activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        try {
            for (id in manager.cameraIdList) {
                val cc = manager.getCameraCharacteristics(id)
                if (cc.get(CameraCharacteristics.LENS_FACING) == 1) {
                    // Get The Sizes of compatible with the requested image
                    val previewSizes = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        ?.getOutputSizes(SurfaceTexture::class.java)
                    if (previewSizes != null) {
                        for (s in previewSizes) {
                            Timber.tag("CameraProcess")
                                .d("showCameraSupportSize: ${s.height} / ${s.width}")
                        }
                    }
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION: Int = 1001
    }
}
