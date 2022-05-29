package com.devthisable.thisable.presentation.feature_object

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devthisable.thisable.R
import com.devthisable.thisable.adapter.ObjectOptionAdapter
import com.devthisable.thisable.analyzer.ObjectAnalyzer
import com.devthisable.thisable.databinding.FragmentObjectDetectionBinding
import com.devthisable.thisable.interfaces.ObjectOptionInterface
import com.devthisable.thisable.utils.*
import com.google.mlkit.vision.objects.ObjectDetection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ObjectDetectionFragment : Fragment() {

    private var binding_: FragmentObjectDetectionBinding? = null
    private val binding: FragmentObjectDetectionBinding get() = binding_!!

    private lateinit var cameraExecutor: ExecutorService

    //private lateinit var camera_capture_button: Button
    private lateinit var graphicOverlay: GraphicOverlay
    private var stateCamera: Boolean = false
    private lateinit var objAnalyzer: ObjectAnalyzer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        graphicOverlay = binding.graphicOverlay
        cameraExecutor = Executors.newSingleThreadExecutor()
        objAnalyzer = ObjectAnalyzer(graphicOverlay, requireContext())
        binding.viewFinder.setOnLongClickListener {
            stateCamera = true
            showAlertDialog()
            true
        }
        binding.viewFinder.setOnClickListener {
            showToastMessage(requireContext(), "Tekan Dan Tahan lama untuk melihat Opsi Pilihan")
            true
        }

        binding.ivBack.setOnClickListener {
            requireActivity().finish()
        }

        if (allPermissionsGranted()) {
            startCamera(false)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding_ = FragmentObjectDetectionBinding.inflate(
            inflater
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        startCamera(false)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showAlertDialog() {
        val alertDialog = Dialog(requireContext())
        val tempArrayList = ServeListQuestion.getListQuestion(requireContext())
        val adapter = ObjectOptionAdapter(tempArrayList)
        val subscriberItemListener: ObjectOptionInterface = object : ObjectOptionInterface {
            override fun onClick(data: String) {
                if (data == requireActivity().getString(R.string.question_1_obj_detection)) {
                    Toast.makeText(
                        requireContext(),
                        "Ketuk dan Tahan Untuk mengetahui ada apa saja didepan anda",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onLongClickListener(data: String) {
                when (data) {
                    requireActivity().getString(R.string.question_1_obj_detection) -> {
                        var itemConfig = objAnalyzer.getCurItemCounter()
                        if (itemConfig.isEmpty()) {
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(1000)
                                itemConfig = objAnalyzer.getCurItemCounter()
                                if (!itemConfig.isEmpty() && itemConfig.size <= 10) {
                                    val interval = (itemConfig.size * 2000) // + 2000 if Necessary
                                    val stringReturned = makeItOneString(countTheObj(itemConfig))
                                    Toast.makeText(
                                        requireContext(),
                                        requireActivity().getString(
                                            R.string.response_1_obj_detection,
                                            stringReturned.dropLast(1)
                                        ),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    //playSoundInInterval(interval.toLong(), getListOfRaw())
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Tidak ada yang terdeteksi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } else {
                            if (itemConfig.size <= 10) {

                                val stringReturned = makeItOneString(countTheObj(itemConfig))
                                Toast.makeText(
                                    requireContext(),
                                    requireActivity().getString(
                                        R.string.response_1_obj_detection,
                                        stringReturned.dropLast(1)
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                                //playSoundInInterval(interval.toLong(), getListOfRaw())
                            }
                        }
                    }
                    // Response 2
                    // Response 3
                }
            }
        }
        alertDialog.setContentView(R.layout.custom_dialog_object)
        adapter.setOnClickItemListener(subscriberItemListener)
        val rvDialog = alertDialog.findViewById(R.id.dialog_rv) as RecyclerView
        rvDialog.layoutManager = LinearLayoutManager(requireContext())
        rvDialog.adapter = adapter
        alertDialog.show()
    }

    private fun startCamera(stateSound: Boolean) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // Preview
            val preview: Preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
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
                    this, cameraSelector, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera(false)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }
    }

    companion object {
        private const val TAG = "Live Object Detector Sample App"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }


}
