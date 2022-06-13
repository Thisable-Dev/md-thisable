package com.devthisable.thisable.presentation.feature_object

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devthisable.thisable.R
import com.devthisable.thisable.adapter.ObjectOptionAdapter
import com.devthisable.thisable.analyzer.ObjectAnalyzer
import com.devthisable.thisable.databinding.FragmentObjectDetectionBinding
import com.devthisable.thisable.interfaces.FeedbackListener
import com.devthisable.thisable.interfaces.ObjectOptionInterface
import com.devthisable.thisable.utils.GraphicOverlay
import com.devthisable.thisable.utils.ServeListQuestion
import com.devthisable.thisable.utils.countTheObj
import com.devthisable.thisable.utils.ext.showToast
import com.devthisable.thisable.utils.makeItOneString
import com.devthisable.thisable.utils.showToastMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ObjectDetectionFragment : Fragment() {

    private var binding_: FragmentObjectDetectionBinding? = null
    private val binding: FragmentObjectDetectionBinding get() = binding_!!
    private lateinit var cameraExecutor: ExecutorService

    //private lateinit var camera_capture_button: Button
    private lateinit var graphicOverlay: GraphicOverlay
    private var stateSound: Boolean = false
    private var stateCamera: Boolean = false
    private lateinit var objAnalyzer: ObjectAnalyzer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //requireContext().showToast(getString(R.string.active_object_detection))

        graphicOverlay = binding.graphicOverlay
        cameraExecutor = Executors.newSingleThreadExecutor()
        objAnalyzer = ObjectAnalyzer(graphicOverlay, requireContext())
        setListener()
        startCamera()
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
        startCamera()
        binding.viewFinder.performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,null)
        binding.viewFinder.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
    }

    private fun setListener() {
        binding.viewFinder.setOnLongClickListener {
            stateCamera = true
            showAlertDialog()
            true
        }
        binding.viewFinder.setOnClickListener {
            showToastMessage(requireContext(), getString(R.string.app_info_tekan_lama))
            true
        }

        binding.ivSoundState.setOnClickListener {
            // Check it
            if (!stateSound) stateSound = true
            else stateSound = false
            feedbackListenerInterface?.onListenFeedback(stateSound)
            changeDrawable()
        }
    }

    private fun changeDrawable() {
        if (stateSound) {
            binding.ivSoundState.setImageDrawable(requireContext().getDrawable(R.drawable.sound_on))
            context?.showToast("Suara Diaktifkan")
        } else {
            binding.ivSoundState.setImageDrawable(requireContext().getDrawable(R.drawable.sound_off))
            context?.showToast("Suara Dimatikan")
        }
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
                        getString(R.string.onClickInfoObjectDetection),
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
                                        getString(R.string.info_noObjectDetected),
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

    private fun startCamera() {
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
                // Ini CUma Buat testing
                val lol = (2  * 23).toString() + 231
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
                Timber.tag(TAG).e(exc, "Use case binding failed")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    companion object {

        private val TAG = ObjectDetectionFragment.toString()

        private var feedbackListenerInterface: FeedbackListener? = null

        fun setFeedbackListener(feedbackListener: FeedbackListener) {
            this.feedbackListenerInterface = feedbackListener
        }
    }
}
