package com.devtedi.tedi.presentation.feature_currency

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import java.io.File

class CurrencyFragment : Fragment(), FeatureBaseline, AnalyzerSubject {

    private var _binding: FragmentCurrencyBinding? = null
    private val binding: FragmentCurrencyBinding get() = _binding!!

    override lateinit var cameraPreviewView: PreviewView
    override lateinit var cameraProcess: CameraProcess
    override lateinit var yolov5TFLiteDetector: YOLOv5ModelCreator

    private lateinit var pref : SharedPrefManager
    private val viewModel : CurrencyDetectionViewModel by viewModels()
    private var observers : ArrayList<AnalyzerObserver> = ArrayList()
    lateinit var fullImageAnalyse : FullImageAnalyse
    private var rotation : Int = 0

    private var soundPlayer: SoundPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundPlayer = SoundPlayer.getInstance(requireContext())
    }

    //Soundplayer Variabels
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCurrencyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SharedPrefManager(requireContext())
        viewModel.initModel(const_currency_detector, File(pref.getCurrencyDetectorPath as String) ,requireContext())
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

        viewModel.isSoundOn.observe(viewLifecycleOwner) { isOn ->
            binding.btnToggleSoundOnOff.setImageResource(if (isOn) R.drawable.sound_on else R.drawable.sound_off)
            Toast.makeText(requireContext(), getString(if (isOn) R.string.info_sound_on else R.string.info_sound_off), Toast.LENGTH_SHORT).show()
        }

        initViews()
    }

    override fun onPause() {
        super.onPause()
        notifyObserver()
    }

    private fun initViews() {
        binding.btnToggleSoundOnOff.setOnClickListener {
            viewModel.toggleSoundOnOff()
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            rotation = requireActivity().windowManager.defaultDisplay.rotation
            viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) { it ->

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
                cameraProcess.startCamera(requireContext(), fullImageAnalyse, cameraPreviewView)
            }
        }
        catch (e : UninitializedPropertyAccessException)
        {
            e.printStackTrace()
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

    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.dispose()
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