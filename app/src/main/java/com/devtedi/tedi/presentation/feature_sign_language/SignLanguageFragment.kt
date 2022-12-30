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
import com.devtedi.tedi.interfaces.observer_keyboard.KeyboardObserver
import com.devtedi.tedi.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.lang.StringBuilder
import java.security.Key
import java.util.*

/**
 *
 * Kelas ini digunakan untuk melakukan deteksi bahasa isyarat menggunakan Machine Learning
 *
 * @constructor untuk buat instance dari SignLanguageFragment.
 */
class SignLanguageFragment : Fragment(), FeatureBaseline, KeyboardObserver{

    private var _binding : FragmentSignLanguageBinding? = null
    private val binding : FragmentSignLanguageBinding get() = _binding!!
    override lateinit var cameraPreviewView: PreviewView
    override lateinit var cameraProcess: CameraProcess
    override lateinit var yolov5TFLiteDetector: YOLOv5ModelCreator
    private val viewModel : SignLanguageViewModel by viewModels()
    private var rotation : Int = 0
    private lateinit var pref : SharedPrefManager

    private lateinit var keyboardListener : FeedbackSignLanguageListener

    private var soundPlayer: SoundPlayer? = null

    lateinit var fullImageAnalyse : FullImageAnalyse

    // Initialize SoundPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundPlayer = SoundPlayer.getInstance(requireContext())
    }

    // Initialize binding
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

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.isGone = !it
        }

        pref = SharedPrefManager(requireContext())

        initPoseListener()
        initKeyboard()
        registerKeyboard()

        cameraProcess = CameraProcess()
        cameraPreviewView = binding.cameraPreviewWrap
        if(!cameraProcess.allPermissionGranted(requireContext())) {
            cameraProcess.requestPermission(requireActivity())
        }
    }

    override fun onResume() {
        super.onResume()

        try {
            // Initialize yoloV5 if null
            if (viewModel.yolov5TFLiteDetector.value == null) {
                viewModel.initModel(const_bisindo_translator, File(pref.getSignLanguagePath as String),requireContext())
            }

            // Observe yoloV5, if exist then initialize and start CameraX
            viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {
                fullImageAnalyse = FullImageAnalyse(
                    requireContext(),
                    cameraPreviewView,
                    rotation,
                    it,
                    graphicOverlay = binding.graphicOverlay,
                    onResult = { label ->
                        soundPlayer?.playSound(label)
                    }
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
    private fun registerKeyboard()
    {
        binding.etOutputTerjemahan.registerObserver(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.dispose()
    }

    private fun initPoseListener() {

        keyboardListener = object : FeedbackSignLanguageListener {
            override fun onListenerKeyboard(state: Boolean) {
                if(state) {
                    openKeyboardDialog()
                }
                else {

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

    override fun updateObserver() {
        viewModel.removePredictionContent()

    }

}