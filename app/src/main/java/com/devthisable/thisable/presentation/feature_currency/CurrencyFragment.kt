package com.devthisable.thisable.presentation.feature_currency

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.devthisable.thisable.R
import com.devthisable.thisable.analyzer.CurrencyAnalyzer
import com.devthisable.thisable.databinding.FragmentCurrencyBinding
import com.devthisable.thisable.interfaces.FeedbackListener
import com.devthisable.thisable.interfaces.ObjectOptionInterface
import com.devthisable.thisable.presentation.feature_object.ObjectDetectionFragment
import com.devthisable.thisable.utils.*
import com.devthisable.thisable.utils.ext.showToast
import java.lang.IllegalStateException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CurrencyFragment : Fragment() {

    private var binding_ : FragmentCurrencyBinding? = null
    private val binding : FragmentCurrencyBinding get() = binding_!!
    private lateinit var cameraExecutor : ExecutorService
    private lateinit var currencyAnalyzer : CurrencyAnalyzer
    private var stateSound : Boolean = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        if (allPermissionGranted()) {
            startCamera()
        }
        else {
            ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSION, PERMISSION_CODE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        binding_ = FragmentCurrencyBinding.inflate(inflater)
        return binding.root
    }

    private fun init() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        currencyAnalyzer = CurrencyAnalyzer(binding.graphicOverlay, requireContext())
        setOnClickListener()
    }


    private fun setOnClickListener() {
        val itemListener = object : ObjectOptionInterface {
            override fun onClick(data: String) {
                when (data) {
                    getString(R.string.question_1_currency_detection) -> {
                        showToastMessage(requireContext(), data)
                    }
                    getString(R.string.question_2_currency_detection) -> {
                        showToastMessage(requireContext(), data)
                    }
                }
            }

            override fun onLongClickListener(data: String) {
                when (data) {
                    getString(R.string.question_1_currency_detection) -> {
                        var items = currencyAnalyzer.getCurrencyDetected()
                        if (!items.isEmpty()) {
                            val returned = makeItOneString(countTheObj(items))
                            showToastMessage(
                                requireActivity(),
                                getString(
                                    R.string.response_1_currency_detection,
                                    returned,
                                    sumTheDetectedCurrency(items).toString()
                                )
                            )
                        }
                    }
                    getString(R.string.question_2_currency_detection) -> {
                        var items = currencyAnalyzer.getCurrencyDetected()
                        val returnedText = makeItOneString(countTheObj(items))
                        showToastMessage(
                            requireContext(),
                            getString(
                                R.string.response_2_currency_detection,
                                sumTheDetectedCurrency(items).toString()
                            )
                        )
                    }
                }
            }
        }
        binding.ivSoundState.setOnClickListener {
            // Check it
            if (!stateSound) stateSound = true
            else stateSound = false
            feedbackListener?.onListenFeedback(stateSound)
            changeDrawable()
        }
        binding.viewFinder.setOnLongClickListener{
            showAlertDialogObjDetection(requireContext(), ServeListQuestion.getListQuestionCurrency(requireContext()),itemListener )
            true
        }

        binding.ivBack.setOnClickListener {
           requireActivity().finish()
        }

        // check If Login
        // TODO
        //
    }

    private fun changeDrawable() {
        if(stateSound) {
            binding.ivSoundState.setImageDrawable(requireContext().getDrawable(R.drawable.ic_baseline_volume_on))
            context?.showToast("Suara Diaktifkan")
        }
        else {
            binding.ivSoundState.setImageDrawable(requireContext().getDrawable(R.drawable.ic_action_volume_off))
            context?.showToast("Suara Dimatikan")
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_CODE) {
            if(allPermissionGranted()) {
                startCamera()
            }
            else {
                showToastMessage(requireContext(), "Permission not Granted")
                requireActivity().finish()
            }
        }
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
                        it.setAnalyzer(cameraExecutor, currencyAnalyzer)
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
    private fun allPermissionGranted() : Boolean {
        return REQUIRED_PERMISSION.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private val PERMISSION_CODE : Int = 10
        private val TAG : String = CurrencyFragment::class.java.simpleName

        private lateinit var feedbackListener: FeedbackListener
        fun setOnFeedbackListener(feedbackListener: FeedbackListener ) {
            this.feedbackListener = feedbackListener
        }
    }

}
