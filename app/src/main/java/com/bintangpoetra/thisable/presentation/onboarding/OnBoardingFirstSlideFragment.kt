package com.bintangpoetra.thisable.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.databinding.FragmentOnboardingFirstBinding
import com.bintangpoetra.thisable.utils.ext.showToast

class OnBoardingFirstSlideFragment: Fragment() {

    private var _fragmentOnBoardingFirstBinding: FragmentOnboardingFirstBinding? = null
    private val binding get() = _fragmentOnBoardingFirstBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _fragmentOnBoardingFirstBinding = FragmentOnboardingFirstBinding.inflate(inflater, container, false)
        return _fragmentOnBoardingFirstBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pager = activity?.findViewById<ViewPager2>(R.id.vp2OnBoarding)
        binding.btnNext.setOnClickListener {
            context?.showToast("btn next clicked")
            pager?.currentItem = 3
        }
        binding.btnCircle2.setOnClickListener {
            context?.showToast("btn circle clicked")
            pager?.currentItem = 1
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _fragmentOnBoardingFirstBinding = null
    }

}