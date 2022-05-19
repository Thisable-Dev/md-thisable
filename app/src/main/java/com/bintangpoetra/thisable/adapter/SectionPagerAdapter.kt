package com.bintangpoetra.thisable.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bintangpoetra.thisable.presentation.feature_currency.CurrencyFragment
import com.bintangpoetra.thisable.presentation.feature_object.ObjectDetectionFragment
import com.bintangpoetra.thisable.presentation.feature_sign_language.SignLanguageFragment
import com.bintangpoetra.thisable.presentation.feature_text.TextDetectionFragment


class SectionPagerAdapter (activity : AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return TOTAL
    }

    override fun createFragment(position: Int): Fragment {
        var fragment : Fragment?= null
        when(position) {
            0 -> fragment = ObjectDetectionFragment()
            1 -> fragment = TextDetectionFragment()
            2 -> fragment = CurrencyFragment()
            3 -> fragment = SignLanguageFragment()
        }

        return fragment as Fragment
    }

    companion object
    {
        private const val TOTAL : Int = 4

    }
}
