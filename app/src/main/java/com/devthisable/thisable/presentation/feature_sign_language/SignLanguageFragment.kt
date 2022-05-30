package com.devthisable.thisable.presentation.feature_sign_language

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.devthisable.thisable.R
import com.devthisable.thisable.analyzer.SignLanguageAnalyzer
import com.devthisable.thisable.databinding.FragmentSignLanguageBinding
import com.devthisable.thisable.interfaces.FeedbackSignLanguageListener
import com.devthisable.thisable.interfaces.SignLanguageListener
import com.devthisable.thisable.interfaces.SignlanguageContentListener
import com.devthisable.thisable.utils.ext.gone
import com.devthisable.thisable.utils.ext.show
import com.devthisable.thisable.utils.showAlertDialogSignLanguage
import com.devthisable.thisable.utils.showToastMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SignLanguageFragment : Fragment() {

    private var binding_ : FragmentSignLanguageBinding? = null
    private val binding : FragmentSignLanguageBinding get() = binding_!!
    private lateinit var signLanguageAnalyzer : SignLanguageAnalyzer
    private lateinit var cameraExecutor : ExecutorService
    private lateinit var keyboardCustomListener: FeedbackSignLanguageListener
    private lateinit var signLanguageListener: SignLanguageListener

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFirebase()
        initUI()

        init()
        startCamera()
    }

    private fun initFirebase() {
        auth = Firebase.auth
    }

    private fun initUI() {
        if (auth.currentUser != null) {
            binding.ivGoogle.gone()
        } else {
            binding.ivGoogle.show()
        }
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
                    Log.d("PRERTTY", state.toString())
                    // Create Interface for listen to The Signlanguage Backend camera
                    subscribeSignlanguageContentListener?.onListenContent(true)
                    // Show dialog
                    showAlertDialogSignLanguage(requireContext(), subscribeSignlanguageContentListener)
                }
                else {
                    // Create Interface for make it
                    subscribeSignlanguageContentListener?.onListenContent(false)
                }
            }

        }

        binding.etOutputTerjemahan.setKeyboardListener(keyboardCustomListener)
    }





    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val runnableInterface = Runnable {
            val cameraProvider : ProcessCameraProvider = cameraProviderFuture.get()
            val preview : Preview = Preview.Builder().build().also {
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


    override fun onResume() {
        super.onResume()
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
