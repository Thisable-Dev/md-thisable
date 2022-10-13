package com.devtedi.tedi.presentation.feature_currency

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
import com.devtedi.tedi.databinding.FragmentCurrencyBinding
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.interfaces.observer_analyzer.AnalyzerObserver
import com.devtedi.tedi.interfaces.observer_analyzer.AnalyzerSubject
import com.devtedi.tedi.utils.*

class CurrencyFragment : Fragment(), FeatureBaseline, AnalyzerSubject {

    private var _binding: FragmentCurrencyBinding? = null
    private val binding: FragmentCurrencyBinding get() = _binding!!

    override lateinit var cameraPreviewView: PreviewView
    override lateinit var cameraProcess: CameraProcess
    override lateinit var yolov5TFLiteDetector: YOLOv5ModelCreator
    private val viewModel : CurrencyDetectionViewModel by viewModels()
    private var observers : ArrayList<AnalyzerObserver> = ArrayList()
    lateinit var fullImageAnalyse : FullImageAnalyse
    private var rotation : Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCurrencyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initModel(const_currency_detector, requireContext())
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isGone = !it
        }
        cameraProcess = CameraProcess()
        cameraPreviewView = binding.cameraPreviewWrap
        if (!cameraProcess.allPermissionGranted(requireContext())) {
            cameraProcess.requestPermission(requireActivity())
        }
        viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {
            initGraphicListenerHandler(it)
        }
    }

    override fun onPause() {
        super.onPause()
        notifyObserver()
    }
    override fun onResume() {
        super.onResume()
        rotation = requireActivity().windowManager.defaultDisplay.rotation
        viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {

            fullImageAnalyse = FullImageAnalyse(
                requireContext(),
                cameraPreviewView,
                rotation,
                it,
                graphicOverlay = binding.graphicOverlay
            )
            cameraProcess.startCamera(requireContext(), fullImageAnalyse, cameraPreviewView)
        }
    }

    private fun initGraphicListenerHandler(modelan : YOLOv5ModelCreator) {
        binding.graphicOverlay.setOnLongClickListener {
            val df = DialogGenerator.newInstance(requireActivity(),
                requireContext().resources.getStringArray(R.array.questionsCurrencyDetection),
                impl_oc_ocl_currency,
                modelan)
            df.show(requireActivity().supportFragmentManager, "dialog")
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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