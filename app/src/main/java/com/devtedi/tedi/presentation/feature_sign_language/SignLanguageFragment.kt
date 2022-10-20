package com.devtedi.tedi.presentation.feature_sign_language

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
import com.devtedi.tedi.databinding.FragmentSignLanguageBinding
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.interfaces.FeedbackSignLanguageListener
import com.devtedi.tedi.interfaces.SignLanguageListener
import com.devtedi.tedi.interfaces.SignlanguageContentListener
import com.devtedi.tedi.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.StringBuilder

class SignLanguageFragment : Fragment(), FeatureBaseline {

    private var _binding : FragmentSignLanguageBinding? = null
    private val binding : FragmentSignLanguageBinding get() = _binding!!

    override lateinit var cameraPreviewView: PreviewView
    override lateinit var cameraProcess: CameraProcess
    override lateinit var yolov5TFLiteDetector: YOLOv5ModelCreator
    private val viewModel : SignLanguageViewModel by viewModels()
    private var rotation : Int = 0

    private lateinit var keyboardListener : FeedbackSignLanguageListener

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

        viewModel.initModel(const_bisindo_translator, requireContext())
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isGone = !it
        }

        initPoseListener()
        initKeyboard()

        cameraProcess = CameraProcess()
        cameraPreviewView = binding.cameraPreviewWrap
        if(!cameraProcess.allPermissionGranted(requireContext())) {
            cameraProcess.requestPermission(requireActivity())
        }

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        try {
            if (viewModel.yolov5TFLiteDetector.value == null) {
                viewModel.initModel(const_bisindo_translator, requireContext())
            }
            viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {
                fullImageAnalyse = FullImageAnalyse(
                    requireContext(),
                    cameraPreviewView,
                    rotation,
                    it,
                    graphicOverlay = binding.graphicOverlay
                )
                it.registerObserver(viewModel)
                cameraProcess.startCamera(
                    requireActivity(),
                    fullImageAnalyse,
                    cameraPreviewView
                )

            }

            viewModel.tobeWrittenString.observe(viewLifecycleOwner) {
                binding.etOutputTerjemahan.setText(StringBuilder().apply {
                    append(it)
                    append(" ")
                })
            }

        }
        catch (e : UninitializedPropertyAccessException)
        {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun initPoseListener() {

        keyboardListener = object : FeedbackSignLanguageListener {
            override fun onListenerKeyboard(state: Boolean) {
                if(state) {
                    subscribeSignlanguageContentListener?.onListenContent(true)
                    openKeyboardDialog()
                }
                else {
                    subscribeSignlanguageContentListener?.onListenContent(false)
                }
            }

        }
    }

    private fun initKeyboard() {
        //binding.btnKeyboard.setOnClickListener(::openKeyboardDialog)
        binding.etOutputTerjemahan.setKeyboardListener(keyboardListener)
    }

    private fun openKeyboardDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext()).apply {
            setContentView(R.layout.custom_keyboard_dialog)
        }

        bottomSheetDialog.show()
    }
    companion object {
        private  var subscribeSignlanguageContentListener: SignlanguageContentListener? = null
        fun setSignLanguageContentListener(signlanguageContentListener: SignlanguageContentListener) {
            this.subscribeSignlanguageContentListener = signlanguageContentListener
        }
    }
}