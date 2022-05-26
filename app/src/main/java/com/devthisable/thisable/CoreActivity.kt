package com.devthisable.thisable

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.StringRes
import com.devthisable.thisable.adapter.SectionPagerAdapter
import com.devthisable.thisable.databinding.ActivityCoreBinding
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
            val view = layoutInflater.inflate(R.layout.custom_tab_layout, null)
            view.findViewById<ImageView>(R.id.icon).setBackgroundResource(TAB_ICON[position])
            tab.setCustomView(view)
        }.attach()
        supportActionBar?.elevation = 0f
        supportActionBar?.hide()

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
        val TAB_ICON = intArrayOf(
            R.drawable.ic_bisindo_translator,
            R.drawable.ic_object_detection,
            R.drawable.ic_currency_detection,
            R.drawable.ic_text_detection
        )

        const val CAMERA_RESULT = 10
    }
}
