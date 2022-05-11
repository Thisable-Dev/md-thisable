package com.bintangpoetra.thisable.presentation.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.databinding.FragmentSplashBinding
import com.bintangpoetra.thisable.utils.ConstVal.SPLASH_DELAY_TIME

class SplashFragment : Fragment() {

    private var _fragmentSplashBinding: FragmentSplashBinding? = null
    private val binding get() = _fragmentSplashBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentSplashBinding = FragmentSplashBinding.inflate(inflater, container, false)
        return _fragmentSplashBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
        }, SPLASH_DELAY_TIME)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentSplashBinding = null
    }
}