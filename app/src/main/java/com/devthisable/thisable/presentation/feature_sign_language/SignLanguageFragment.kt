package com.devthisable.thisable.presentation.feature_sign_language

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devthisable.thisable.analyzer.SignLanguageAnalyzer
import com.devthisable.thisable.databinding.FragmentSignLanguageBinding
import com.devthisable.thisable.interfaces.FeedbackSignLanguageListener
import com.devthisable.thisable.interfaces.SignLanguageListener
import com.devthisable.thisable.interfaces.SignlanguageContentListener
import com.devthisable.thisable.utils.showAlertDialogSignLanguage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SignLanguageFragment : Fragment() {

    private var binding_ : FragmentSignLanguageBinding? = null
    private val binding : FragmentSignLanguageBinding get() = binding_!!
    private lateinit var signLanguageAnalyzer : SignLanguageAnalyzer
    private lateinit var cameraExecutor : ExecutorService
    private lateinit var keyboardCustomListener: FeedbackSignLanguageListener
    private lateinit var signLanguageListener: SignLanguageListener

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //requireContext().showToast(getString(R.string.active_bisindo))

        init()
        startCamera()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding_ = FragmentSignLanguageBinding.inflate(inflater)
        return binding.root
    }


    private fun init() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        signLanguageListener = object : SignLanguageListener {
            override fun onChangedPose(data: String) {
                val currentText = binding.etOutputTerjemahan.text.toString()
                binding.etOutputTerjemahan.setText( currentText +" "+data.toString() )
            }
        }
        signLanguageAnalyzer = SignLanguageAnalyzer(binding.graphicOverlay, requireContext())
        signLanguageAnalyzer.setSignLanguageListener(signLanguageListener)
        keyboardCustomListener = object : FeedbackSignLanguageListener {
            override fun onListenerKeyboard(state: Boolean) {
                if (state) {
                    subscribeSignlanguageContentListener?.onListenContent(true)
                    // Show dialog
                    showAlertDialogSignLanguage(requireContext(), subscribeSignlanguageContentListener)
                }
                else {
                    // Create Interface for make it unsub
                    subscribeSignlanguageContentListener?.onListenContent(false)
                }
            }
        }
        binding.etOutputTerjemahan.setKeyboardListener(keyboardCustomListener)
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        val metrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(metrics)
        // define the screen size

        val runnableInterface = Runnable {
            val cameraProvider : ProcessCameraProvider = cameraProviderFuture.get()
            val preview : Preview = Preview.Builder()
                .build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, signLanguageAnalyzer)
                    }
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            }
            catch (e : Exception){
                e.printStackTrace()
            }
        }
        cameraProviderFuture.addListener(runnableInterface, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onPause() {
        super.onPause()
        startCamera()
    }

    override fun onResume() {
        super.onResume()
        binding.ccSignLanguage.performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,null)
        binding.ccSignLanguage.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        startCamera()
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private val PERMISSION_CODE : Int = 10

        private  var subscribeSignlanguageContentListener: SignlanguageContentListener? = null
        fun setSignLanguageContentListener(signlanguageContentListener: SignlanguageContentListener) {
            this.subscribeSignlanguageContentListener = signlanguageContentListener
        }
    }
}
