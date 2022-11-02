package com.devtedi.tedi.presentation.home

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.devtedi.tedi.R
import com.devtedi.tedi.data.dummy.BannerDummy.getBannerList
import com.devtedi.tedi.databinding.FragmentHomeBinding
import com.devtedi.tedi.interfaces.observer_cloud.CloudModelObserver
import com.devtedi.tedi.interfaces.observer_cloudstorage.CloudStorageObserver
import com.devtedi.tedi.presentation.feature_cloud.CloudModel
import com.devtedi.tedi.presentation.feature_cloud.CloudStorage
import com.devtedi.tedi.utils.*
import com.devtedi.tedi.utils.dialogs.DialogAgreementCreator
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.showCustomDialog
import com.devtedi.tedi.utils.ext.showCustomToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment(), CloudModelObserver, CloudStorageObserver {

    private var _fragmentHomeBinding: FragmentHomeBinding? = null
    private val binding get() = _fragmentHomeBinding!!
    private lateinit var prefs: SharedPrefManager

    private var modelCounterDownload: Int = 0
    private var labelCounterDownload: Int = 0

    private var booleanLabelsDownloaded: Boolean = true
    private var booleanModelDownloaded: Boolean = true

    private var isCurrentlyDownloading: Boolean = false
    private var isConnectedToInternet: Boolean = false

    private lateinit var auth: FirebaseAuth

    private lateinit var connectivityStatus: InternetConnectivityLiveData


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return _fragmentHomeBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = SharedPrefManager(requireContext())
        CloudModel.registerObserver(this)
        CloudStorage.registerObserver(this)
        initFirebase()
        initUI()
        loadBanner()
        initAction()
        if (prefs.getIsModelUpdate && isWifiOn()) {
            showCustomDialog(
                "Yo",
                "yo",
                "yo",
                "No",
                onClickPositive = { dialogHandlerYes() },
                onClickNegative = { dialogHandlerNo() }
            )
        }
    }

    private fun dialogHandlerNo() {

    }

    private fun dialogHandlerYes() {
        Log.d("BLOKPRIM", "asdas")
        observeConnectivity()
        isConnectedToInternet = true
    }

    private fun checkLatestModel() {
        if (isAllLabelDownloaded(prefs)) {
            booleanLabelsDownloaded = false
            CloudStorage.getLabelFilesFromCloud()
        }

        if (isAllModelDownloaded(prefs)) {
            booleanModelDownloaded = false
            CloudModel.downloadLatestSignlanguageModel()
            CloudModel.downloadLatestObjectDetectionModel()
            CloudModel.downloadLatestCurrencyDetectionModel()
        }

        if (!booleanModelDownloaded && !booleanLabelsDownloaded) showDownloadUI(true)
    }

    private fun isWifiOn(): Boolean {
        val wifi: WifiManager =
            requireActivity().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if (wifi.isWifiEnabled) return true
        return false

    }

    private fun showRetryDialog(isGone: Boolean) {
        binding.errorOverlay.isGone = isGone
    }

    private fun observeConnectivity() {
        connectivityStatus = InternetConnectivityLiveData(requireContext())

        val observer = Observer<InternetConnectivityLiveData.Status> {
            when (it) {
                InternetConnectivityLiveData.Status.Connected -> {
                    isConnectedToInternet = true
                    isCurrentlyDownloading = true
                    if (isConnectedToInternet && isCurrentlyDownloading) {
                        modelCounterDownload = 0
                        labelCounterDownload = 0
                        checkLatestModel()
                        showRetryDialog(true)
                    }
                }

                InternetConnectivityLiveData.Status.NotConnected -> {
                    showRetryDialog(false)
                    isCurrentlyDownloading = false
                    isConnectedToInternet = false
                }
            }
        }
        connectivityStatus.observe(viewLifecycleOwner, observer)
    }


    private fun initFirebase() {
        auth = Firebase.auth
    }

    private fun initUI() {
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvBanner)
        binding.rvBanner.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val name = auth.currentUser?.displayName.toString().split(" ")
        binding.tvWelcomeWithName.text = getString(R.string.label_welcome_home, name[0])
    }

    private fun validatePermission(): Boolean {
        for (permission in ConstVal.arrayOfPermissions) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
        }
        return true
    }

    private fun initAction() {
        var bundleData = Bundle()

        if (isAllModelDownloaded(prefs) && isAllLabelDownloaded(prefs)) {
            Log.d("BLOGPRIM", prefs.getSignLanguagePath.toString())
            binding.btnObjectDetection.click {
                bundleData.putString(
                    EXTRA_DATA_HOME,
                    resources.getString(R.string.action_object_detection)
                )
                findNavController().navigate(
                    R.id.action_homeFragment_to_coreActivity,
                    args = bundleData
                )
            }
            binding.btnTextDetection.click {
                bundleData.putString(
                    EXTRA_DATA_HOME,
                    resources.getString(R.string.action_text_detection)
                )
                findNavController().navigate(
                    R.id.action_homeFragment_to_coreActivity,
                    args = bundleData
                )
            }
            binding.btnCurrencyDetection.click {
                bundleData.putString(
                    EXTRA_DATA_HOME,
                    resources.getString(R.string.action_currency_detection)
                )
                findNavController().navigate(
                    R.id.action_homeFragment_to_coreActivity,
                    args = bundleData
                )
            }
            binding.btnColorDetection.click {
                bundleData.putString(
                    EXTRA_DATA_HOME,
                    resources.getString(R.string.action_color_detection)
                )
                findNavController().navigate(
                    R.id.action_homeFragment_to_coreActivity,
                    args = bundleData
                )
            }
            binding.btnBisindoTranslator.click {
                findNavController().navigate(R.id.action_homeFragment_to_coreActivity)
            }
            binding.btnFamilyHelp.click {
                findNavController().navigate(R.id.action_homeFragment_to_familyHelpFragment)
            }
        }
    }

    private fun loadBanner() {
        val adapter = BannerAdapter(requireContext(), getBannerList())
        binding.rvBanner.adapter = adapter
    }

    private fun showDownloadUI(showUi: Boolean) {
        if (showUi) {
            showCustomToast(getString(R.string.info_text_pembaharuan))
            binding.tvPembaharuan.visibility = View.VISIBLE
            binding.viewBgDownload.visibility = View.VISIBLE
            binding.pbLoadingModel.visibility = View.VISIBLE
            binding.viewBgDownload.setOnClickListener {
                showCustomToast(
                    getString(
                        R.string.info_pengunduhan,
                        modelCounterDownload.toString(),
                        TOTAL_MODEL.toString()
                    )
                )
            }
        } else {
            binding.viewBgDownload.visibility = View.INVISIBLE
            binding.pbLoadingModel.visibility = View.INVISIBLE
            binding.tvPembaharuan.visibility = View.INVISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentHomeBinding = null
    }

    // CloudModel Interface Implementation

    private fun handleModelDownload() {
        modelCounterDownload += 1
        showCustomToast(
            getString(
                R.string.info_pengunduhan,
                modelCounterDownload.toString(),
                TOTAL_MODEL.toString()
            )
        )
        //Log.d("DOWNLOADTAGS", "$modelCounterDownload / 3")
        if (modelCounterDownload == TOTAL_MODEL) {
            showCustomToast("Downloading Successfully ")
            setModelPreference(prefs)
            setUpdateModelPreference(prefs)
            booleanModelDownloaded = true
            prefs = SharedPrefManager(requireContext())
            // btnState()
            showDownloadUI(false)
        }
    }

    private fun handleModelFailureDownload() {
        showDownloadUI(false)
    }

    override fun updateObserver() {
        handleModelDownload()
    }

    override fun updateFailureObserver(message: String) {

        showCustomToast(message)
        showRetryDialog(false)
        handleModelFailureDownload()
    }

    // CloudModel Interface End Implementation


    //CloudStorage Interface Implementation

    private fun handleLabelDownload() {
        labelCounterDownload += 1
        if (labelCounterDownload == TOTAL_LABEL) {
            setLabelsPreference(prefs)
            booleanLabelsDownloaded = true
        }
    }


    private fun handleLabelFailureDownload() {

    }

    override fun updateObserverCloudStorageSuccess() {
        handleLabelDownload()
    }

    override fun updateObserverCloudStorageFailure() {
        handleLabelFailureDownload()
    }

    // Cloud Storage Interface Implementatio End

    companion object {
        const val EXTRA_DATA_HOME: String = "EXTRA_DATA"
        const val TOTAL_MODEL: Int = 3
        const val TOTAL_LABEL: Int = 3
    }

}