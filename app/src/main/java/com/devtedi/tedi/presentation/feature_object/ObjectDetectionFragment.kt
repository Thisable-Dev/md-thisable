package com.devtedi.tedi.presentation.feature_object

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devtedi.tedi.R
import com.devtedi.tedi.analysis.FullImageAnalyse
import com.devtedi.tedi.databinding.FragmentObjectDetectionBinding
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.interfaces.observer_analyzer.AnalyzerObserver
import com.devtedi.tedi.interfaces.observer_analyzer.AnalyzerSubject
import com.devtedi.tedi.presentation.feature_cloud.CloudModel
import com.devtedi.tedi.utils.*
import java.io.File

class ObjectDetectionFragment : Fragment(), FeatureBaseline, AnalyzerSubject{

    private var _binding: FragmentObjectDetectionBinding? = null
    private val binding: FragmentObjectDetectionBinding get() = _binding!!

    private val viewModel: ObjectDetectionViewModel by viewModels()

    override lateinit var cameraPreviewView: PreviewView
    override lateinit var yolov5TFLiteDetector: YOLOv5ModelCreator
    override lateinit var cameraProcess: CameraProcess
    private lateinit var fullImageAnalyse: FullImageAnalyse
    private lateinit var pref : SharedPrefManager

    private val observers : ArrayList<AnalyzerObserver> = ArrayList()

    private var soundPlayer: SoundPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundPlayer = SoundPlayer.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentObjectDetectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SharedPrefManager(requireContext())
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isGone = !isLoading
        }

        cameraProcess = CameraProcess()
        if (!cameraProcess.allPermissionGranted(requireContext())) {
            cameraProcess.requestPermission(requireActivity())
        }
        cameraPreviewView = binding.cameraPreviewWrap

        viewModel.isSoundOn.observe(viewLifecycleOwner) { isOn ->
            binding.btnToggleSoundOnOff.setImageResource(if (isOn) R.drawable.sound_on else R.drawable.sound_off)
        }

        binding.btnToggleSoundOnOff.setOnClickListener {
            viewModel.toggleSoundOnOff()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (viewModel.yolov5TFLiteDetector.value == null) {
                viewModel.initModel(const_object_detector, File(pref.getObjectDetectorPath as String) ,requireContext())
            }
            val rotation = requireActivity().windowManager.defaultDisplay.rotation
            viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) { model ->
                model?.let {
                    fullImageAnalyse = FullImageAnalyse(
                        requireContext(),
                        cameraPreviewView,
                        rotation,
                        it,
                        graphicOverlay = binding.graphicOverlay,
                        onResult = { label ->
                            if (viewModel.isSoundOn.value == true) {
                                soundPlayer?.playSound(label)
                            }
                        }
                    )
                    cameraProcess.startCamera(
                        requireActivity(),
                        fullImageAnalyse,
                        cameraPreviewView
                    )
                }
            }

            viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {
                initGraphicListenerHandler(it)
            }
        }
        catch (e : UninitializedPropertyAccessException)
        {
            e.printStackTrace()
        }
    }

    override fun onPause()
    {
        super.onPause()
        notifyObserver()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.dispose()
    }

    private fun initGraphicListenerHandler(modelan : YOLOv5ModelCreator) {
        binding.graphicOverlay.setOnLongClickListener {
            val df = DialogGenerator.newInstance(requireActivity(),
                requireContext().resources.getStringArray(R.array.questionsObjectDetection),
                impl_oc_ocl_obj,
                modelan)
            df.show(requireActivity().supportFragmentManager, "dialog")
            true
        }
    }


    override fun registerObserver(o: AnalyzerObserver) {
        observers.add(o)
    }

    override fun removeObserver(o: AnalyzerObserver) {
        observers.remove(o)
    }

    override fun notifyObserver() {
        for (o in observers)
        {
            o.updateObserver()
        }
    }
}