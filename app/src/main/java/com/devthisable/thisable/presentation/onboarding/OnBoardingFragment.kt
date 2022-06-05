package com.devthisable.thisable.presentation.onboarding

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.devthisable.thisable.R
import com.devthisable.thisable.databinding.FragmentOnboardingBinding
import com.devthisable.thisable.utils.ConstVal
import com.devthisable.thisable.utils.SharedPrefManager
import com.devthisable.thisable.utils.ext.popTap
import com.devthisable.thisable.utils.ext.showToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class OnBoardingFragment : Fragment() {

    private lateinit var oneTapClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

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

        initAuth()
        initAction()
    }

    private fun initAuth() {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client))
            .requestEmail()
            .build()

        oneTapClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = Firebase.auth
    }

    private fun initAction() {
        binding.btnLoginGoogle.setOnClickListener {
            it.popTap()
            signIn()
        }
        binding.btnNext.setOnClickListener {
            it.findNavController().navigate(R.id.action_onBoardingFragment_to_fragmentWarningCustomDialog)
        }
        binding.tvAbout.setOnClickListener {
            it.popTap()
            it.findNavController().navigate(R.id.action_onBoardingFragment_to_aboutFragment)
        }
        binding.tvTermAgreement.setOnClickListener {
            it.findNavController().navigate(R.id.action_onBoardingFragment_to_termAgreementFragment)
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
                    context?.showToast("Error occured")
                }
            }
    }

}