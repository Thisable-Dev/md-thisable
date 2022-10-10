package com.devtedi.tedi.presentation.feature_sign_language

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devtedi.tedi.analysis.FullImageAnalyse
import com.devtedi.tedi.databinding.FragmentSignLanguageBinding
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.utils.*

class SignLanguageFragment : Fragment(), FeatureBaseline {

    private var _binding : FragmentSignLanguageBinding? = null
    private val binding : FragmentSignLanguageBinding get() = _binding!!

    override lateinit var cameraPreviewView: PreviewView
    override lateinit var cameraProcess: CameraProcess
    override lateinit var yolov5TFLiteDetector: YOLOv5ModelCreator
    private val viewModel : SignLanguageViewModel by viewModels()
    private var rotation : Int = 0
    lateinit var fullImageAnalyse : FullImageAnalyse
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignLanguageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rotation = requireActivity().windowManager.defaultDisplay.rotation
        viewModel.initModel(const_object_detector,requireContext())
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isGone = !it
        }
        cameraProcess = CameraProcess()
        cameraPreviewView = binding.cameraPreviewWrap
        if(!cameraProcess.allPermissionGranted(requireContext())) {
            cameraProcess.requestPermission(requireActivity())
        }

        viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {
            initGraphicListenerHandler(it)
        }
    }

    override fun onResume() {
        super.onResume()

        if(viewModel.yolov5TFLiteDetector.value == null) {
            viewModel.initModel(const_test_model, requireContext())
        }
        viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {
            fullImageAnalyse = FullImageAnalyse(
                requireContext(),
                cameraPreviewView,
                rotation,
                it,
                graphicOverlay = binding.graphicOverlay
            )
            cameraProcess.startCamera(requireActivity(), fullImageAnalyse, cameraPreviewView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initGraphicListenerHandler(modelan : YOLOv5ModelCreator) {
        binding.graphicOverlay.setOnLongClickListener {
            val df = DialogGenerator.newInstance(requireActivity(),
                getObjConstTemp(),
                impl_oc_ocl_obj,
                modelan)
            df.show(requireActivity().supportFragmentManager, "dialog")
            true
        }
    }
}