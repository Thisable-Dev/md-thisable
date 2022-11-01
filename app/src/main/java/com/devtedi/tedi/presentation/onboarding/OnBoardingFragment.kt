package com.devtedi.tedi.presentation.onboarding

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.FragmentOnboardingBinding
import com.devtedi.tedi.interfaces.observer_cloud.CloudModelObserver
import com.devtedi.tedi.interfaces.observer_cloudstorage.CloudStorageObserver
import com.devtedi.tedi.presentation.feature_cloud.CloudModel
import com.devtedi.tedi.presentation.feature_cloud.CloudStorage
import com.devtedi.tedi.utils.*
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.io.File
import java.lang.NullPointerException

class OnBoardingFragment : Fragment(), CloudModelObserver, CloudStorageObserver {

    private lateinit var oneTapClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private var modelCounterDownload: Int = 0
    private var labelCounterDownload: Int = 0

    private var booleanModelDownloaded: Boolean = true
    private var booleanLabelsDownloaded: Boolean = true
    private var isCurrentlyDownloading : Boolean = false
    private var isConnectedToInternet : Boolean = false

    private var _fragmentOnBoardingBinding: FragmentOnboardingBinding? = null
    private val binding get() = _fragmentOnBoardingBinding!!

    private var counter = 0
    private var isNotgranted = 0
    private lateinit var pref: SharedPrefManager

    private lateinit var connectivityStatus: InternetConnectivityLiveData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentOnBoardingBinding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return _fragmentOnBoardingBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SharedPrefManager(requireContext())
        CloudModel.registerObserver(this)
        CloudStorage.registerObserver(this)
        initAuth()
        askPermission()
        initAction()
        //btnState()
    }

    private fun observeConnectivity() {
        connectivityStatus = InternetConnectivityLiveData(requireContext())

        val observer = Observer<InternetConnectivityLiveData.Status> {
            when (it) {
                InternetConnectivityLiveData.Status.Connected -> {
                    isConnectedToInternet = true
                    isCurrentlyDownloading = true
                    if(isConnectedToInternet && isCurrentlyDownloading) {
                        modelCounterDownload = 0
                        prepareTheModel()
                        showRetryDialog(true)
                    }
                }
                InternetConnectivityLiveData.Status.NotConnected -> {
                    showRetryDialog(false)
                    isCurrentlyDownloading = false
                    isConnectedToInternet = false
                    showToast(getString(R.string.info_no_wifi_connection))
                }
                null -> {}
            }
        }
        connectivityStatus.observe(viewLifecycleOwner, observer)
    }

    private fun validatePermission() : Boolean
    {
        for ( permission in ConstVal.arrayOfPermissions)
        {
            if ( ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED)
            {
                return false
            }
        }
        return true
    }

    private fun prepareTheModel() {
        if (pref.getObjectDetectorPath.isNullOrEmpty() &&
            pref.getCurrencyDetectorPath.isNullOrEmpty() &&
            pref.getSignLanguagePath.isNullOrEmpty()
        ) {
            booleanModelDownloaded = false
            showToast("Downloading the model ...")

            CloudModel.downloadObjectDetectionModel()
            CloudModel.downloadCurrencyDetectionModel()
            CloudModel.downloadSignLanguageModel()

            if (pref.getSignLanguageLabelPath.isNullOrEmpty() &&
                pref.getCurrencyDetectorLabelPath.isNullOrEmpty() &&
                pref.getObjectDetectorLabelPath.isNullOrEmpty()
            ) {
                booleanLabelsDownloaded = false
                CloudStorage.getLabelFilesFromCloud()
        }
            if (!booleanModelDownloaded && !booleanLabelsDownloaded) showDownloadUI(true)
        }
    }

    private fun showDownloadUI(state: Boolean) {
        if (state) {
            binding.viewBgDownload.visibility = View.VISIBLE
            binding.pbLoadingModel.visibility = View.VISIBLE
            binding.tvInfoModelLoading.visibility = View.VISIBLE
            binding.viewBgDownload.setOnClickListener {
                Toast.makeText(requireContext(),
                    "StillDownloading Model, ${if(modelCounterDownload <= 3) modelCounterDownload else {} } / 3",
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.viewBgDownload.visibility = View.INVISIBLE
            binding.pbLoadingModel.visibility = View.INVISIBLE
            binding.tvInfoModelLoading.visibility = View.INVISIBLE
        }
    }

    private fun initAuth() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client))
            .requestServerAuthCode(getString(R.string.default_web_client))
            .requestEmail()
            .build()

        oneTapClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = Firebase.auth
    }

    private fun checkPermission(requestPermissionLauncher: ActivityResultLauncher<String>) {
        ConstVal.arrayOfPermissions.forEach {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireContext(),it
                ),
                -> {}
                else -> {
                 when(it) {
                     Manifest.permission.CAMERA -> {
                         val dialog: AlertDialog = AlertDialog.Builder(requireContext())
                             .setTitle(getString(R.string.title_warning))
                             .setMessage(getString(R.string.message_ask_camera_permission))
                             .setPositiveButton(getString(R.string.action_give_access)) { _, _ ->
                                 requestPermissionLauncher.launch(it)
                             }
                             .setNegativeButton(getString(R.string.action_no)) { _, _ ->
                                 showToast(getString(R.string.message_app_will_close))
                                 requireActivity().finish()
                             }.create()
                         dialog.show()
                     }
                     Manifest.permission.WRITE_EXTERNAL_STORAGE ->
                     {
                         val dialog: AlertDialog = AlertDialog.Builder(requireContext())
                             .setTitle(getString(R.string.title_warning))
                             .setMessage(getString(R.string.message_ask_camera_permission))
                             .setPositiveButton(getString(R.string.action_give_access)) { _, _ ->
                                 requestPermissionLauncher.launch(it)
                             }
                             .setNegativeButton(getString(R.string.action_no)) { _, _ ->
                                 showToast(getString(R.string.message_app_will_close))
                                 requireActivity().finish()
                             }.create()
                         dialog.show()
                     }
                     else -> {

                     }
                 }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(isAllLabelDownloaded(pref) && isAllModelDownloaded(pref)) {
            showDownloadUI(false)
        }
    }

    private fun askPermission() {
        val requestPermissionLauncher: ActivityResultLauncher<String> =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { is_granted: Boolean ->
                if (is_granted) {
                    counter += 1
                    if(counter == ConstVal.arrayOfPermissions.size)
                    {
                        if (!setupTheCloudModelStorage(pref)) {
                            if (!isConnectedToInternet)
                                showRetryDialog(false)
                            if (!isAllLabelDownloaded(pref) && !isAllModelDownloaded(pref))
                                observeConnectivity()
                        }
                    }
                }
                else if (!is_granted){
                    isNotgranted += 1
                    if(isNotgranted > 0 )
                        requireActivity().finish()
                }
            }
        checkPermission(requestPermissionLauncher)
    }
    /*
        private fun btnState()
        {
            if(modelCounterDownload != 3)
            {
                binding.btnLoginGoogle.isEnabled = false
                binding.btnNext.isEnabled = false
            }
            if (modelCounterDownload == 3 || booleanModelDownloaded)
            {
                binding.btnLoginGoogle.isEnabled = true
                binding.btnNext.isEnabled = true
            }
        }
     */
    private fun initAction() {
        binding.btnLoginGoogle.click {
            if(validatePermission()) {
                if(isAllLabelDownloaded(pref) && isAllModelDownloaded(pref) )
                {
                    signIn()
                }
                else {
                    if (!setupTheCloudModelStorage(pref)) {
                        if (!isConnectedToInternet)
                            showRetryDialog(false)
                        if (!isAllLabelDownloaded(pref) && !isAllModelDownloaded(pref))
                            observeConnectivity()
                    }
                }
            }
            else
            {
                askPermission()
            }
        }
        binding.btnNext.click {
            if(validatePermission()) {
                if(isAllLabelDownloaded(pref) && isAllModelDownloaded(pref) ) {
                    findNavController().navigate(R.id.action_onBoardingFragment_to_coreActivity)
                }
                else
                {
                    Log.d("DEBUGTAGSGAN", pref.getSignLanguagePath.toString())
                    if (!setupTheCloudModelStorage(pref)) {
                        if (!isConnectedToInternet)
                            showRetryDialog(false)
                        if (!isAllLabelDownloaded(pref) && !isAllModelDownloaded(pref))
                            observeConnectivity()
                    }
                }
            }
            else {
                askPermission()
            }
        }
        binding.tvAbout.click {
            findNavController().navigate(R.id.action_onBoardingFragment_to_aboutFragment)

        }
        binding.tvTermAgreement.click {
            findNavController().navigate(R.id.action_onBoardingFragment_to_termAgreementFragment)
        }
        binding.btnRetry.click {
            if(!isCurrentlyDownloading && isConnectedToInternet) {
                modelCounterDownload = 0
                labelCounterDownload = 0
                showRetryDialog(true)
                prepareTheModel()
            }
            else if(!isCurrentlyDownloading && !isConnectedToInternet)
            {
                showRetryDialog(false)
                showToast("Pastikan Internet anda aktif")
            }
        }
    }

    private fun signIn() {
        val signInIntent = oneTapClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Timber.d("firebaseAuthWithGoogle : ${account.id}")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.w("Google sign in failed : $e")
            }
        } else {
            showToast("Login Failed $result")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Timber.d("sign in with credential success")
                    val user = auth.currentUser
                    pref.apply {
                        setStringPreference(ConstVal.KEY_USER_ID, user?.uid.toString())
                        setStringPreference(ConstVal.KEY_USER_NAME, user?.displayName.toString())
                        setStringPreference(ConstVal.KEY_EMAIL, user?.email.toString())
                        setBooleanPreference(ConstVal.KEY_IS_LOGIN, true)
                    }
                    findNavController().navigate(R.id.action_onBoardingFragment_to_instructionFragment)
                } else {
                    Timber.w("sign in with credential failure ${task.exception}")
                    showToast("Error occurred")
                }
            }
            .addOnFailureListener {
                Timber.e("<<<<<<<<<<<, Error $it")
            }
    }

    override fun updateObserver() {
        handleDownloadModels()
    }

    private fun handleDownloadModels() {
        modelCounterDownload += 1
        try {
            showToast("Downloading model $modelCounterDownload / 3")
            //Log.d("DOWNLOADTAGS", "$modelCounterDownload / 3")
            if (modelCounterDownload == TOTAL_MODEL) {
                showToast("Downloading Successfully ")
                setModelPreference(pref)
                pref = SharedPrefManager(requireContext())
                booleanModelDownloaded = true
                // btnState()
                showDownloadUI(false)
                initAction()
            }
        }
        catch (e : NullPointerException) {
            e.printStackTrace()
        }
    }

    private fun handleDownloadLabels() {
        labelCounterDownload += 1

        try {
            if (labelCounterDownload == TOTAL_LABEL) {
                setLabelsPreference(pref)
                booleanLabelsDownloaded = true
            }
        }
        catch (e : NullPointerException )
        {
            e.printStackTrace()
        }
    }

    override fun updateFailureObserver(message: String) {
        showToast(message)
        showDownloadUI(false)
        showRetryDialog(false)
    }

    private fun showRetryDialog(isGone: Boolean) {
        binding.errorOverlay.setOnClickListener {}
        binding.errorOverlay.isGone = isGone
    }

    override fun updateObserverCloudStorageSuccess() {
        handleDownloadLabels()
    }

    override fun updateObserverCloudStorageFailure() {

    }

    companion object {
        private const val TOTAL_LABEL: Int = 3
        private const val TOTAL_MODEL: Int = 3


    }
}