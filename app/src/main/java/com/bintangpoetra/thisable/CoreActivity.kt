package com.bintangpoetra.thisable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.StringRes
import com.bintangpoetra.thisable.adapter.SectionPagerAdapter
import com.bintangpoetra.thisable.databinding.ActivityCoreBinding
import com.google.android.material.tabs.TabLayoutMediator

class CoreActivity : AppCompatActivity() {
    private lateinit  var binding : ActivityCoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionPagerAdapter = SectionPagerAdapter(this)
        binding.viewPager.adapter = sectionPagerAdapter
        val tabs = binding.tabsCoreFeature
        val viewPager = binding.viewPager
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
        supportActionBar?.elevation = 0f
    }
    /*
        fun performCloudVisionRequest(uri : Uri) {
            if (uri != null) {
                try{
                    val bitmap = getTheScreenShot()
                    callCloudVision(bitmap)
                    showToastMessage(this, "TODOMESSAGE")
                }
                catch (e : IOException) {
                    Log.e("DAN", e.message.toString())
                }
            }
        }
    */
    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.tab_text_1,
            R.string.tab_text_2,
            R.string.tab_text_3,
            R.string.tab_text_4
        )
        const val CAMERA_RESULT = 10
    }
}
