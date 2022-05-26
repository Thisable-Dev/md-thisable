package com.devthisable.thisable.presentation.feature_sign_language

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.devthisable.thisable.R
import com.devthisable.thisable.analyzer.SignLanguageAnalyzer
import com.devthisable.thisable.databinding.FragmentSignLanguageBinding
import com.devthisable.thisable.utils.showToastMessage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SignLanguageFragment : Fragment() {

    private var binding_ : FragmentSignLanguageBinding? = null
    private val binding : FragmentSignLanguageBinding get() = binding_!!
    private lateinit var cameraExecutor : ExecutorService
    private lateinit var signLanguageAnalyzer : SignLanguageAnalyzer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        if (allPermissionGranted()) {
            startCamera()
        }
        else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSION, PERMISSION_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding_ = FragmentSignLanguageBinding.inflate(inflater)
        return binding.root
    }

    private fun init() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        signLanguageAnalyzer = SignLanguageAnalyzer(binding.graphicOverlay, requireContext())
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
                showToastMessage(requireContext(), "Permission not Granted")
                requireActivity().finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
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
                        it.setAnalyzer(cameraExecutor, signLanguageAnalyzer)
                    }
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
        cameraProviderFuture.addListener(runnableInterface, ContextCompat.getMainExecutor(requireContext()))
    }


    override fun onResume() {
        super.onResume()
        startCamera()
    }
    private fun allPermissionGranted() : Boolean {
        return REQUIRED_PERMISSION.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }


    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private val PERMISSION_CODE : Int = 10
    }
}
