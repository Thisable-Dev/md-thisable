package com.devtedi.tedi.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devtedi.tedi.presentation.feature_currency.CurrencyFragment
import com.devtedi.tedi.presentation.feature_object.ObjectDetectionFragment
import com.devtedi.tedi.presentation.feature_sign_language.SignLanguageFragment
import com.devtedi.tedi.presentation.feature_text.TextDetectionFragment


class SectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return TOTAL
    }

    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment? = when (position) {
            0 -> ColorDetectorFragment()
            1 -> SignLanguageFragment()
            2 -> CurrencyFragment()
            3 -> ColorDetectorFragment()
            4 -> ColorDetectorFragment()
            else -> null
        }
        return fragment as Fragment
    }

    companion object {
        private const val TOTAL: Int = 5
    }
}
