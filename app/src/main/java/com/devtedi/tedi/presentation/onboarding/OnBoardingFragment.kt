package com.devtedi.tedi.presentation.onboarding

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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devtedi.tedi.R
import com.devtedi.tedi.R.string
import com.devtedi.tedi.databinding.FragmentOnboardingBinding
import com.devtedi.tedi.interfaces.observer_cloud.CloudModelObserver
import com.devtedi.tedi.interfaces.observer_cloudstorage.CloudStorageObserver
import com.devtedi.tedi.presentation.feature_cloud.CloudModel
import com.devtedi.tedi.presentation.feature_cloud.CloudStorage
import com.devtedi.tedi.utils.ConstVal
import com.devtedi.tedi.utils.SharedPrefManager
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.enable
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

class OnBoardingFragment : Fragment(), CloudModelObserver, CloudStorageObserver {

    private lateinit var oneTapClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private var modelCounterDownload : Int = 0
    private var labelCounterDownload : Int = 0

    private var booleanModelDownloaded : Boolean = true
    private var booleanLabelsDownloaded : Boolean = true

    private var _fragmentOnBoardingBinding: FragmentOnboardingBinding? = null
    private val binding get() = _fragmentOnBoardingBinding!!

    private lateinit var pref: SharedPrefManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentOnBoardingBinding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return _fragmentOnBoardingBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SharedPrefManager(requireContext())
        CloudModel.registerObserver(this)
        initAuth()
        initAction()
        askPermission()
        //btnState()
    }

    fun checkLatestModel()
    {

    }

    private fun prepareTheModel()
    {
        if( pref.getObjectDetectorPath.isNullOrEmpty()  &&
            pref.getCurrencyDetectorPath.isNullOrEmpty()  &&
            pref.getSignLanguagePath.isNullOrEmpty())
        {
            booleanModelDownloaded = false
            showToast("Downloading the model ...")

            CloudModel.downloadObjectDetectionModel()
            CloudModel.downloadCurrencyDetectionModel()
            CloudModel.downloadSignLanguageModel()

            if ( pref.getSignLanguageLabelPath.isNullOrEmpty() &&
                pref.getCurrencyDetectorLabelPath.isNullOrEmpty() &&
                pref.getObjectDetectorLabelPath.isNullOrEmpty())
            {
                booleanLabelsDownloaded = false

                CloudStorage.getLabelFilesFromCloud()

            }
            if(!booleanModelDownloaded && !booleanLabelsDownloaded) UIDownloadState(true)
        }


    }

    private fun UIDownloadState(state : Boolean)
    {
        if(state)
        {
            binding.viewBgDownload.visibility = View.VISIBLE
            binding.pbLoadingModel.visibility = View.VISIBLE
            binding.tvInfoModelLoading.visibility = View.VISIBLE
            binding.viewBgDownload.setOnClickListener {
                Toast.makeText(requireContext(), "StillDownloading Model, $modelCounterDownload / 3", Toast.LENGTH_SHORT).show()
            }
        }
        else
        {
            binding.viewBgDownload.visibility = View.INVISIBLE
            binding.pbLoadingModel.visibility = View.INVISIBLE
            binding.tvInfoModelLoading.visibility = View.INVISIBLE
        }
    }
    private fun initAuth() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(string.default_web_client))
            .requestServerAuthCode(getString(string.default_web_client))
            .requestEmail()
            .build()

        oneTapClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = Firebase.auth
    }

    private fun checkPermission(requestPermissionLauncher: ActivityResultLauncher<String>) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) -> {
                prepareTheModel()
            }
            else -> {
                val dialog: AlertDialog = AlertDialog.Builder(requireContext())
                    .setTitle(getString(string.title_warning))
                    .setMessage(getString(string.message_ask_camera_permission))
                    .setPositiveButton(getString(string.action_give_access)) { _, _ ->
                        requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                    .setNegativeButton(getString(string.action_no)) { _, _ ->
                        showToast(getString(string.message_app_will_close))
                        requireActivity().finish()
                    }.create()
                dialog.show()
            }
        }
    }

    private fun askPermission() {
        val requestPermissionLauncher: ActivityResultLauncher<String> =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { is_granted: Boolean ->
                if (is_granted) {
                    //
                    prepareTheModel()
                    showToast(getString(string.message_now_you_can_use_the_app))
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
            signIn()
        }
        binding.btnNext.click {
            findNavController().navigate(R.id.action_onBoardingFragment_to_coreActivity)
        }
        binding.tvAbout.click {
            findNavController().navigate(R.id.action_onBoardingFragment_to_aboutFragment)
        }
        binding.tvTermAgreement.click {
            findNavController().navigate(R.id.action_onBoardingFragment_to_termAgreementFragment)
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
            showToast("Login Failed ${result}")
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

    private fun handleDownloadModels()
    {
        modelCounterDownload += 1
        showToast("Downloading model $modelCounterDownload / 3")
        //Log.d("DOWNLOADTAGS", "$modelCounterDownload / 3")
        if(modelCounterDownload == TOTAL_MODEL)
        {
            showToast("Downloading Successfully ")

            if(CloudModel.fileObjectDetection != null)
                pref.setStringPreference(ConstVal.LOCAL_MODEL_PATH_OD, CloudModel.fileObjectDetection!!.path as String )

            if(CloudModel.fileCurrencyDetection != null)
                pref.setStringPreference(ConstVal.LOCAL_MODEL_PATH_CD, CloudModel.fileCurrencyDetection!!.path as String)

            if(CloudModel.fileSignLanguage != null )
                pref.setStringPreference(ConstVal.LOCAL_MODEL_PATH_SL, CloudModel.fileSignLanguage!!.path as String)

            booleanModelDownloaded = true
            // btnState()
            UIDownloadState(false)
        }
    }

    private fun handleDownloadLabels()
    {
        labelCounterDownload += 1

        if(labelCounterDownload == TOTAL_LABEL)
        {
            if(CloudStorage.labelFileCurrencyDetection != null)
            {
                pref.setStringPreference(ConstVal.LOCAL_LABEL_MODEL_PATH_CD, CloudStorage.labelFileObjectDetection as String)
            }

            if(CloudStorage.labelFileObjectDetection != null)
            {
                pref.setStringPreference(ConstVal.LOCAL_LABEL_MODEL_PATH_OD, CloudStorage.labelFileObjectDetection as String)
            }

            if(CloudStorage.labelFileSignLanguage != null)
            {
                pref.setStringPreference(ConstVal.LOCAL_LABEL_MODEL_PATH_SL, CloudStorage.labelFileSignLanguage as String)
            }

            booleanLabelsDownloaded = true
        }
    }

    override fun updateFailureObserver() {

    }

    override fun updateObserverCloudStorageSuccess() {

        handleDownloadLabels()
    }

    override fun updateObserverCloudStorageFailure() {

    }
    companion object {
        private const val TOTAL_LABEL : Int = 3
        private const val TOTAL_MODEL : Int = 3
    }
}