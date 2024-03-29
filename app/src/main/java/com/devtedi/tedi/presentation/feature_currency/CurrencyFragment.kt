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
import com.devtedi.tedi.utils.dialogs.DialogGenerator
import com.devtedi.tedi.utils.ext.showCustomToast
import java.io.File

/**
 *
 * Kelas ini digunakan untuk melakukan deteksi mata uang menggunakan Machine Learning
 *
 * @constructor untuk buat instance dari CurrencyFragment.
 */
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

    // Initialize SoundPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        soundPlayer = SoundPlayer.getInstance(requireContext())
    }

    // Initialize binding
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

        // Observe yoloV5 model, jika sudah ada modelnya, maka lanjut inisialisasi GraphicListenerHandler
        viewModel.yolov5TFLiteDetector.observe(viewLifecycleOwner) {
            initGraphicListenerHandler(it)
        }

        // Observe state isSoundOn, update UI berdaasarkan state dan menampilkan toast.
        viewModel.isSoundOn.observe(viewLifecycleOwner) { isOn ->
            binding.btnToggleSoundOnOff.setImageResource(if (isOn) R.drawable.sound_on else R.drawable.sound_off)
            showCustomToast(getString(if (isOn) R.string.info_sound_on else R.string.info_sound_off))
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
                        // Result dari analisis akan diputar suaranya oleh SoundPlayer
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