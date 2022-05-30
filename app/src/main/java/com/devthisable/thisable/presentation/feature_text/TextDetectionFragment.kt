package com.devthisable.thisable.presentation.feature_text

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devthisable.thisable.analyzer.TextDetectionAnalyzer
import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.visionapi.model.FeatureItem
import com.devthisable.thisable.data.remote.visionapi.model.ImageItem
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionRequest
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionRequestItem
import com.devthisable.thisable.databinding.FragmentTextDetectionBinding
import com.devthisable.thisable.interfaces.ObjectOptionInterface
import com.devthisable.thisable.utils.ConstVal.API_KEY
import com.devthisable.thisable.utils.FrameMetadata
import com.devthisable.thisable.utils.ServeListQuestion
import com.devthisable.thisable.utils.ext.showToast
import com.devthisable.thisable.utils.showAlertDialogObjDetection
import com.devthisable.thisable.utils.showToastMessage
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class TextDetectionFragment : Fragment() {

    private val viewModel: TextDetectionViewModel by viewModels()

    private lateinit var binding_: FragmentTextDetectionBinding
    private val binding get() = binding_
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private lateinit var textDetectionAnalyzer: TextDetectionAnalyzer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        startCamera()
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
        startCamera()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding_ = FragmentTextDetectionBinding.inflate(inflater)
        return binding.root
    }

    private fun init() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        textDetectionAnalyzer = TextDetectionAnalyzer(requireContext())
        setOnClickListener()
    }
    private fun showUIPB(state : String )  {
        if ( state == "upload") {
            binding.pbUpload.visibility = View.VISIBLE
            binding.tvInfo.text = "Uploading"
            binding.tvInfo.visibility = View.VISIBLE
        }

    }

    private fun setOnClickListener() {
        val itemListener = object : ObjectOptionInterface {
            override fun onClick(data: String) {
                //TODO
            }

            override fun onLongClickListener(data: String) {
                val image = textDetectionAnalyzer.getDetectedImage()
                if (image != null) {
                    val currImage = image

                    val byteArrayOutputStream = ByteArrayOutputStream()
                    currImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

                    // gambar dalam bentuk byte
                    val imageByte = byteArrayOutputStream.toByteArray()
                    val base64encoded = Base64.encodeToString(imageByte, Base64.NO_WRAP)

                    val textDetectionRequest = TextDetectionRequest(
                        request = TextDetectionRequestItem(
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

                    //context?.showToast(base64encoded)
                    //Log.d("base64image", base64encoded)

                    textDetection(API_KEY, textDetectionRequest)

                   // showToastMessage(requireContext(), "Bitmap IS NOT NULL!!!!")
                } else {
                    showToastMessage(requireContext(), "Bitmap Is NULL WTF")
                }
            }
        }
        binding.viewFinder.setOnLongClickListener {
            showAlertDialogObjDetection(
                requireContext(),
                ServeListQuestion.getListQuestion(requireContext()),
                subscriberItemListener = itemListener
            )
            true
        }

        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun textDetection(apiKey: String, request: TextDetectionRequest) {
        viewModel.textDetection(apiKey, request).observe(viewLifecycleOwner) { response ->
            when (response) {
                is ApiResponse.Loading -> {
                    showUIPB("upload")
                    //context?.showToast("Loading.......")
                }
                is ApiResponse.Success -> {
                    context?.showToast(response.data.responses[0].fullTextAnnotation.toString())
                }
                is ApiResponse.Error -> {
                    context?.showToast(response.errorMessage)
                    //context?.showToast("Error occured")
                }
            }
        }
    }
}
