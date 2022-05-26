package com.devthisable.thisable.presentation.feature_text

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.devthisable.thisable.CoreActivity
import com.devthisable.thisable.analyzer.TextDetectionAnalyzer
import com.devthisable.thisable.databinding.FragmentTextDetectionBinding
import com.devthisable.thisable.interfaces.ObjectOptionInterface
import com.devthisable.thisable.utils.*
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class TextDetectionFragment : Fragment() {

    private lateinit var binding_ : FragmentTextDetectionBinding
    private val binding get() = binding_
    private lateinit var cameraExecutor : ExecutorService
    private var imageCapture : ImageCapture? = null
    private lateinit var textDetectionAnalyzer : TextDetectionAnalyzer


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val runnableInterface = Runnable {
            val cameraProvider = cameraProviderFuture.get()
            val preview : Preview = Preview.Builder().build().also {
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
            }catch (e : Exception) {
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

    private fun init () {
        cameraExecutor = Executors.newSingleThreadExecutor()
        textDetectionAnalyzer = TextDetectionAnalyzer(requireContext())
        setOnClickListener()
    }

    private fun setOnClickListener() {
        val itemListener = object : ObjectOptionInterface {
            override fun onClick(data: String) {
                //TODO
            }

            override fun onLongClickListener(data: String) {
                val image = textDetectionAnalyzer.getDetectedImage()
                if(image  != null) {
                    val metadata = FrameMetadata(image.width, image.height, 0)
                    val currImage = image
                    Log.d("YOWEU", currImage.toString())
                    binding.ivVolume.setImageBitmap(currImage)
                    showToastMessage(requireContext(), "Bitmap IS NOT NULL!!!!")
                }
                else {
                    showToastMessage(requireContext(), "Bitmap Is NULL WTF")
                }
            }

        }
        binding.viewFinder.setOnLongClickListener {
            showAlertDialogObjDetection(requireContext(), ServeListQuestion.getListQuestion(requireContext()), subscriberItemListener = itemListener)
            true
        }
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile  = createFile(requireActivity())

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()), object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val intent = Intent()
                    intent.putExtra("picture", photoFile)
                    requireActivity().setResult(CoreActivity.CAMERA_RESULT,intent)
                    Log.d("TESTINGBT","BETE")
                    requireActivity().finish()
                }

                override fun onError(exception: ImageCaptureException) {
                    showToastMessage(requireContext(),"Gagal Mengambil Gambar")
                }
            }
        )
    }
}
