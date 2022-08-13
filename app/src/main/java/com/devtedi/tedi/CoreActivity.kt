package com.devtedi.tedi

import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.devtedi.tedi.adapter.SectionPagerAdapter
import com.devtedi.tedi.databinding.ActivityCoreBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoreActivity : AppCompatActivity() {
    private lateinit  var binding : ActivityCoreBinding
    private var argsBinding : String? = null
    private lateinit var tabs : TabLayout
    private lateinit var viewPager : ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoreBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionPagerAdapter = SectionPagerAdapter(this)
        binding.viewPager.adapter = sectionPagerAdapter
        argsBinding = intent.extras?.getString("EXTRA_DATA")
        tabs = binding.tabsCoreFeature
        viewPager = binding.viewPager
        if (argsBinding != null) {
            changeTabLayoutIndicator()
        }
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            val view = layoutInflater.inflate(R.layout.custom_tab_layout, null)
            view.findViewById<ImageView>(R.id.icon).setBackgroundResource(TAB_ICON[position])
            view.findViewById<ImageView>(R.id.icon).contentDescription =  getString(CONTENT_DESC[position])
            tab.customView = view
        }.attach()

        supportActionBar?.elevation = 0f
        supportActionBar?.hide()

        binding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun changeTabLayoutIndicator() {
        if (argsBinding == this.getString(R.string.action_object_detection)) {
            tabs.getTabAt(1)?.select()
            viewPager.currentItem = 1
        }
        if (argsBinding == resources.getString(R.string.action_currency_detection)) {
            tabs.getTabAt(2)?.select()
            viewPager.currentItem = 2
        }
        if (argsBinding == resources.getString(R.string.action_text_detection)) {
            tabs.getTabAt(3)?.select()
            viewPager.currentItem =3
        }

    }

    companion object {
        @StringRes
        val TAB_ICON = intArrayOf(
            R.drawable.ic_bisindo_translator,
            R.drawable.ic_object_detection,
            R.drawable.ic_currency_detection,
            R.drawable.ic_text_detection
        )
        @StringRes
        val CONTENT_DESC = intArrayOf(
            R.string.desc_bisindo,
            R.string.desc_object_detection,
            R.string.desc_currency,
            R.string.text_detection
        )

    }
}
