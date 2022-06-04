package com.devthisable.thisable.presentation.instruction

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class InstructionViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = InstructionFirstSlideFragment()
            1 -> fragment = InstructionSecondSlideFragment()
        }
        return fragment as Fragment
    }

}