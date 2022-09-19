package com.devtedi.tedi.presentation.feature_currency

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.devtedi.tedi.R
import com.devtedi.tedi.R.string
import com.devtedi.tedi.analyzer.CurrencyAnalyzer
import com.devtedi.tedi.databinding.FragmentCurrencyBinding
import com.devtedi.tedi.interfaces.FeedbackListener
import com.devtedi.tedi.interfaces.ObjectOptionInterface
import com.devtedi.tedi.utils.ServeListQuestion
import com.devtedi.tedi.utils.countTheObj
import com.devtedi.tedi.utils.ext.showToast
import com.devtedi.tedi.utils.makeItOneString
import com.devtedi.tedi.utils.showAlertDialogObjDetection
import com.devtedi.tedi.utils.sumTheDetectedCurrency
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CurrencyFragment : Fragment() {

    private var _fragmentCurrencyBinding: FragmentCurrencyBinding? = null
    private val binding: FragmentCurrencyBinding get() = _fragmentCurrencyBinding!!
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var currencyAnalyzer: CurrencyAnalyzer
    private var stateSound: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCurrencyBinding = FragmentCurrencyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //requireContext().showToast(getString(R.string.active_currency_detection))

        init()
        initPermission()
    }

    private fun init() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        currencyAnalyzer = CurrencyAnalyzer(binding.graphicOverlay, requireContext())
        setOnClickListener()
    }

    private fun initPermission() {
        if (allPermissionGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSION, PERMISSION_CODE)
        }
    }

    private fun setOnClickListener() {
        val itemListener = object : ObjectOptionInterface {
            override fun onClick(data: String) {
                when (data) {
                    getString(string.question_1_currency_detection) -> {
                        showToast(data)
                    }
                }
            }

            override fun onLongClickListener(data: String) {
                when (data) {
                    getString(string.question_1_currency_detection) -> {
                        val items = currencyAnalyzer.getCurrencyDetected()
                        if (items.isNotEmpty()) {
                            val returned = makeItOneString(countTheObj(items))
                            showToast(
                                getString(
                                    R.string.response_1_currency_detection,
                                    returned,
                                    sumTheDetectedCurrency(items).toString()
                                )
                            )
                        }
                    }
                }
            }
        }
        binding.ivSoundState.setOnClickListener {
            // Check it
            stateSound = !stateSound
            feedbackListener.onListenFeedback(stateSound)
            changeDrawable()
        }
        binding.viewFinder.setOnLongClickListener {
            showAlertDialogObjDetection(requireContext(), ServeListQuestion.getListQuestionCurrency(requireContext()), itemListener)
            true
        }
    }

    private fun changeDrawable() {
        if (stateSound) {
            binding.ivSoundState.setImageDrawable(requireContext().getDrawable(R.drawable.sound_on))
            showToast(getString(string.message_sound_activated))
        } else {
            binding.ivSoundState.setImageDrawable(requireContext().getDrawable(R.drawable.sound_off))
            showToast(getString(string.message_sound_deactivated))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (allPermissionGranted()) {
                startCamera()
            } else {
                showToast(getString(string.message_permisson_not_granted))
                requireActivity().finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        val runnableInterface = Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview: Preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val imageAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, currencyAnalyzer)
                    }
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        cameraProviderFuture.addListener(runnableInterface, ContextCompat.getMainExecutor(requireContext()))
    }

    override fun onResume() {
        super.onResume()

        binding.viewFinder.performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
        binding.viewFinder.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED)
        startCamera()
    }

    private fun allPermissionGranted(): Boolean {
        return REQUIRED_PERMISSION.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {

        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val PERMISSION_CODE: Int = 10
        private val TAG: String = CurrencyFragment::class.java.simpleName

        private lateinit var feedbackListener: FeedbackListener
        fun setOnFeedbackListener(feedbackListener: FeedbackListener) {
            this.feedbackListener = feedbackListener
        }
    }
}
