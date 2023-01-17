package com.devtedi.tedi.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.devtedi.tedi.presentation.feature_color.ColorFragment
import com.devtedi.tedi.presentation.feature_currency.CurrencyFragment
import com.devtedi.tedi.presentation.feature_object.ObjectDetectionFragment
import com.devtedi.tedi.presentation.feature_sign_language.SignLanguageFragment
import com.devtedi.tedi.presentation.feature_text.TextDetectionFragment

/**
 *
 * Kelas ini digunakan sebagai adapter dari ViewPager, yang menampilkan halaman deteksi menggunakan kamera.
 *
 * @param activity dibutuhkan oleh FragmentStateAdapter untuk membuat instance.
 * @constructor untuk buat instance dari SectionPagerAdapter.
 */
class SectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    /**
     * Fungsi yang dibutuhkan oleh FragmentStateAdapter untuk mengetahui jumlah halaman yang akan dirender.
     * @return mengembalikan ukuran list data yang akan dipakai FragmentStateAdapter untuk merender halaman.
     */
    override fun getItemCount(): Int {
        return TOTAL
    }

    /**
     * Fungsi yang digunakan untuk membuat Fragment yang akan ditampilkan pada ViewPager.
     * @return mengembalikan object Fragment berdasarkan posisi halaman.
     */
    override fun createFragment(position: Int): Fragment {
        val fragment: Fragment? = when (position) {
            0 -> SignLanguageFragment()
            1 -> ObjectDetectionFragment()
            2 -> CurrencyFragment()
            3 -> TextDetectionFragment()
            4 -> ColorFragment()
            else -> null
        }
        return fragment as Fragment
    }

    companion object {
        private const val TOTAL: Int = 5
    }
}
