package com.devtedi.tedi.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devtedi.tedi.R
import com.devtedi.tedi.R.string
import com.devtedi.tedi.databinding.FragmentProfileBinding
import com.devtedi.tedi.utils.ConstVal.KEY_EMAIL
import com.devtedi.tedi.utils.ConstVal.KEY_EMERGENCY_CONTACT
import com.devtedi.tedi.utils.ConstVal.KEY_IS_LOGIN
import com.devtedi.tedi.utils.ConstVal.KEY_TOKEN
import com.devtedi.tedi.utils.ConstVal.KEY_USER_ID
import com.devtedi.tedi.utils.ConstVal.KEY_USER_NAME
import com.devtedi.tedi.utils.SharedPrefManager
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.popTap
import com.devtedi.tedi.utils.ext.setImageUrl
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private var _fragmentProfileBinding: FragmentProfileBinding? = null
    private val binding get() = _fragmentProfileBinding!!

    private lateinit var pref: SharedPrefManager
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
        return _fragmentProfileBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = SharedPrefManager(requireContext())

        auth = Firebase.auth

        val gso = Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        initUI()
        initAction()
    }

    private fun initUI() {
        binding.tvFullName.text = pref.getUserName
        binding.tvFullEmail.text = pref.getEmail
        binding.imgProfile.setImageUrl(auth.currentUser?.photoUrl.toString())
    }

    private fun initAction() {
        binding.btnFqa.click {
            findNavController().navigate(R.id.action_profileFragment_to_fragmentFQA)
        }
        binding.btnHelp.click {
            findNavController().navigate(R.id.action_profileFragment_to_helpFragment)
        }
        binding.btnReportBug.click {
            findNavController().navigate(R.id.action_profileFragment_to_bugReportFragment)
        }
        binding.btnAbout.click {
            findNavController().navigate(R.id.action_profileFragment_to_aboutFragment)

        }
        binding.btnLogout.click {
            showLogoutDialog()
        }
    }

    private fun logout() {
        auth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener {
            pref.apply {
                clearPreferenceByKey(KEY_USER_ID)
                clearPreferenceByKey(KEY_TOKEN)
                clearPreferenceByKey(KEY_USER_NAME)
                clearPreferenceByKey(KEY_IS_LOGIN)
                clearPreferenceByKey(KEY_EMAIL)
                clearPreferenceByKey(KEY_EMERGENCY_CONTACT)
            }
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(string.message_information))
            setMessage(getString(string.message_logout_confirmation))
            setPositiveButton("OK") { _, _ ->
                try {
                    logout()
                } finally {
                    findNavController().navigate(R.id.action_profileFragment_to_onBoardingFragment)
                }
            }
            setNegativeButton("Batal") { p0, _ ->
                p0.dismiss()
            }
        }.create().show()
    }
}