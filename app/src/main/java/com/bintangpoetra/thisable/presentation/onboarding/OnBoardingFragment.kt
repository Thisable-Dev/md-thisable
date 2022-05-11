package com.bintangpoetra.thisable.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bintangpoetra.thisable.databinding.FragmentOnboardingBinding

class OnBoardingFragment: Fragment() {

    private var _fragmentOnBoardingBinding: FragmentOnboardingBinding? = null
    private val binding get() = _fragmentOnBoardingBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentOnBoardingBinding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return _fragmentOnBoardingBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vpAdapter = OnBoardingViewPagerAdapter(requireActivity())
        binding.vp2OnBoarding.adapter = vpAdapter
    }

}