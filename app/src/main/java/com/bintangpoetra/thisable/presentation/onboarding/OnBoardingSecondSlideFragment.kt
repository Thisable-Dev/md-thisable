package com.bintangpoetra.thisable.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.databinding.FragmentOnboardingSecondBinding
import com.bintangpoetra.thisable.utils.ConstVal.KEY_IS_INTRO
import com.bintangpoetra.thisable.utils.SharedPrefManager

class OnBoardingSecondSlideFragment: Fragment() {

    private var _fragmentOnBoardingSecond: FragmentOnboardingSecondBinding? = null
    private val binding get() = _fragmentOnBoardingSecond!!

    private lateinit var pager: ViewPager2
    private lateinit var pref: SharedPrefManager


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentOnBoardingSecond = FragmentOnboardingSecondBinding.inflate(inflater, container, false)
        return _fragmentOnBoardingSecond?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = SharedPrefManager(requireContext())
        pager = activity?.findViewById(R.id.vp2OnBoarding)!!
        initAction()
    }

    private fun initAction() {
        binding.btnFinish.setOnClickListener {
            it.findNavController().navigate(R.id.action_onBoardingFragment_to_loginFragment)
            pref.apply {
                setBooleanPreference(KEY_IS_INTRO, true)
            }
        }
        binding.btnCircle1.setOnClickListener {
            pager.setCurrentItem(0, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentOnBoardingSecond = null
    }

}