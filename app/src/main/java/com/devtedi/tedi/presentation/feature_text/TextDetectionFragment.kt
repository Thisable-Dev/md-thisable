package com.devtedi.tedi.presentation.feature_text

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devtedi.tedi.analyzer.TextDetectionAnalyzer
import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.data.remote.visionapi.model.FeatureItem
import com.devtedi.tedi.data.remote.visionapi.model.ImageItem
import com.devtedi.tedi.data.remote.visionapi.model.TextDetectionRequest
import com.devtedi.tedi.data.remote.visionapi.model.TextDetectionRequestItem
import com.devtedi.tedi.databinding.FragmentTextDetectionBinding
import com.devtedi.tedi.presentation.dialog.TextDetectionResultDialogFragment
import com.devtedi.tedi.utils.ConstVal.API_KEY
import com.devtedi.tedi.utils.FrameMetadata
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.disable
import com.devtedi.tedi.utils.ext.enable
import com.devtedi.tedi.utils.ext.gone
import com.devtedi.tedi.utils.ext.show
import com.devtedi.tedi.utils.ext.showToast
import com.devtedi.tedi.utils.scaleBitmapDown
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class TextDetectionFragment : Fragment() {

    private val viewModel: TextDetectionViewModel by viewModels()

    private lateinit var _fragmentTextDetectionBinding: FragmentTextDetectionBinding
    private val binding get() = _fragmentTextDetectionBinding
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var textDetectionAnalyzer: TextDetectionAnalyzer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAnalyzer()
        initAction()
        startCamera()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _fragmentTextDetectionBinding = FragmentTextDetectionBinding.inflate(inflater)
        return binding.root
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val runnableInterface = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview: Preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().also {
                    it.setAnalyzer(cameraExecutor, textDetectionAnalyzer)
                }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cameraProviderFuture.addListener(runnableInterface, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onResume() {
        super.onResume()
        binding.viewFinder.performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
        binding.viewFinder.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        startCamera()
    }

    private fun initAnalyzer() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        textDetectionAnalyzer = TextDetectionAnalyzer(requireContext())
    }

    private fun initAction() {
        binding.btnCapture.click {
            val image = textDetectionAnalyzer.getDetectedImage()
            if (image != null) {
                val metadata = FrameMetadata(image.width, image.height, 0)
                val currImage = scaleBitmapDown(image, 640)

                val byteArrayOutputStream = ByteArrayOutputStream()
                currImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

                val imageByte = byteArrayOutputStream.toByteArray()
                val base64encoded = Base64.encodeToString(imageByte, Base64.NO_WRAP)

                val textDetectionRequest = TextDetectionRequest(
                    requests = TextDetectionRequestItem(
                        image = ImageItem(
                            content = base64encoded
                        ),
                        features = listOf(
                            FeatureItem(
                                type = "TEXT_DETECTION",
                                maxResults = 1
                            )
                        )
                    )
                )
                textDetection(API_KEY, textDetectionRequest)
            }
        }
    }

    private fun textDetection(apiKey: String, request: TextDetectionRequest) {
        viewModel.textDetection(apiKey, request).observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    checkLoading(true)
                }
                is ApiResponse.Success -> {
                    checkLoading(false)
                    TextDetectionResultDialogFragment.newInstance(
                        response.data.responses[0].fullTextAnnotation.text
                    ).show(childFragmentManager, TextDetectionResultDialogFragment::class.java.simpleName)
                }
                is ApiResponse.Error -> {
                    checkLoading(false)
                    Timber.e("Error visionapi : ${response.errorMessage}")
                    showToast(response.errorMessage)
                }
                else -> {}
            }
        }
    }

    private fun checkLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbUpload.show()
            binding.tvInfo.text = "Processing"
            binding.tvInfo.show()
            binding.btnCapture.disable()
        } else {
            binding.pbUpload.gone()
            binding.tvInfo.gone()
            binding.btnCapture.enable()
        }
    }
}
