package com.devtedi.tedi.presentation.feature_object

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.devtedi.tedi.analysis.FullImageAnalyse
import com.devtedi.tedi.databinding.FragmentObjectDetectionBinding
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.utils.*

class ObjectDetectionFragment : Fragment(), FeatureBaseline {

    private var _binding: FragmentObjectDetectionBinding? = null
    private val binding: FragmentObjectDetectionBinding get() = _binding!!

    private val viewModel: ObjectDetectionViewModel by viewModels()

    override lateinit var cameraPreviewView: PreviewView
    override lateinit var yolov5TFLiteDetector: YOLOv5ModelCreator
    override lateinit var cameraProcess: CameraProcess
    private lateinit var fullImageAnalyse: FullImageAnalyse

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentObjectDetectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initModel(const_object_detector, requireContext())

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isGone = !isLoading
        }

        cameraProcess = CameraProcess()
        if (!cameraProcess.allPermissionGranted(requireContext())) {
            cameraProcess.requestPermission(requireActivity())
        }

        cameraPreviewView = binding.cameraPreviewWrap

        viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {
            initGraphicListenerHandler(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.yolov5TFLiteDetector.value == null)
        {
            viewModel.initModel(const_object_detector, requireContext())
        }
        val rotation = requireActivity().windowManager.defaultDisplay.rotation
        viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) { model ->
            model?.let {
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
    }

    override fun onStop() {
        super.onStop()
        viewModel.closeModel()
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