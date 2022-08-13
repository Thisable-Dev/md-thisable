package com.devtedi.tedi.presentation.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.FragmentSplashBinding
import com.devtedi.tedi.utils.ConstVal.SPLASH_DELAY_TIME
import com.devtedi.tedi.utils.SharedPrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment() {

    private var _fragmentSplashBinding: FragmentSplashBinding? = null
    private val binding get() = _fragmentSplashBinding!!

    private lateinit var pref: SharedPrefManager
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false)
        return _fragmentSplashBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SharedPrefManager(requireContext())
        val isLogin = pref.isLogin

        auth = Firebase.auth
        val user = auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            when {
                isLogin -> {
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
                user == null -> {
                    findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
                }
            }
        }, SPLASH_DELAY_TIME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentSplashBinding = null
    }
}