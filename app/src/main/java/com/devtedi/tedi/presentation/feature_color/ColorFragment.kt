package com.devtedi.tedi.presentation.feature_color

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.devtedi.tedi.R
import com.devtedi.tedi.analyzer.ColorDetectionAnalyzer
import com.devtedi.tedi.databinding.FragmentColorBinding
import com.devtedi.tedi.factory.ColorGenerator
import com.devtedi.tedi.utils.SoundPlayer
import com.devtedi.tedi.utils.scaleBitmapDown
import org.opencv.android.OpenCVLoader
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ColorFragment : Fragment() {

    private lateinit var binding : FragmentColorBinding
    private lateinit var colorAnalyzer : ColorDetectionAnalyzer
    private lateinit var colorGenerator: ColorGenerator
    private lateinit var cameraExecutor : ExecutorService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentColorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OpenCVLoader.initDebug()
        initConfiguration()
        initAction()
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }
    private fun initConfiguration()
    {
        cameraExecutor = Executors.newSingleThreadExecutor()
        colorAnalyzer = ColorDetectionAnalyzer(requireContext())
    }

    private fun initAction() {
        binding.btnCapture.setOnClickListener {
            val image = colorAnalyzer.getDetectedImage()
            SoundPlayer.getInstance(requireContext()).playSound("anjing")
            if (image != null)
            {
                val currImage = scaleBitmapDown(image, maxDimension)
                colorGenerator = ColorGenerator(requireActivity(), currImage)

            }
        }

    }

    private fun startCamera() {
        try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

            val runnableInterface = Runnable {

                val cameraProvider = cameraProviderFuture.get()
                val preview: Preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.cameraPreviewWrap.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build().also {
                        it.setAnalyzer(cameraExecutor, colorAnalyzer)
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            cameraProviderFuture.addListener(
                runnableInterface,
                ContextCompat.getMainExecutor(requireContext())
            )
        }
        catch (e : UninitializedPropertyAccessException)
        {
            Toast.makeText(context, "Mohon tunggu sebentar, fitur sedang disiapkan", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        private const val maxDimension : Int = 640
    }


}