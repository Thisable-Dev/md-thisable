package com.devtedi.tedi.presentation.feature_color

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devtedi.tedi.R
import com.devtedi.tedi.analyzer.ColorDetectionAnalyzer
import com.devtedi.tedi.databinding.FragmentColorBinding
import com.devtedi.tedi.factory.ColorGenerator
import com.devtedi.tedi.utils.SoundPlayer
import com.devtedi.tedi.utils.scaleBitmapDown
import org.opencv.android.OpenCVLoader
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 *
 * Fragment untuk deteksi warna menggunakan Machine Learning
 *
 * @property binding view binding dari ColorFragment
 * @property colorAnalyzer analyzer warna untuk cameraX
 * @constructor untuk buat instance dari CloudModelAdapter.
 */
class ColorFragment : Fragment() {

    private lateinit var binding: FragmentColorBinding
    private lateinit var colorAnalyzer: ColorDetectionAnalyzer
    private lateinit var colorGenerator: ColorGenerator
    private lateinit var cameraExecutor: ExecutorService
    private var soundPlayer: SoundPlayer? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentColorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OpenCVLoader.initDebug()
        initConfiguration()
        initAction()
        soundPlayer = SoundPlayer.getInstance(requireContext())
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    private fun initConfiguration() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        colorAnalyzer = ColorDetectionAnalyzer(requireContext())
    }

    private fun initAction() {
        binding.btnCapture.setOnClickListener {
            val image = colorAnalyzer.getDetectedImage()
            soundPlayer?.playSound(requireContext().resources.getString(R.string.raw_sound_take_pict))
            if (image != null) {
                val currImage = scaleBitmapDown(image, maxDimension)
                colorGenerator = ColorGenerator(requireActivity(), currImage)
            }
        }

    }

    // Setup camera untuk analisis warna, for more details see CameraX docs.
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
        } catch (e: UninitializedPropertyAccessException) {
            Toast.makeText(context,
                "Mohon tunggu sebentar, fitur sedang disiapkan",
                Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val maxDimension: Int = 640
    }


}