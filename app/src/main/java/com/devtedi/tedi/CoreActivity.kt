package com.devtedi.tedi

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.devtedi.tedi.adapter.SectionPagerAdapter
import com.devtedi.tedi.databinding.ActivityCoreBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoreBinding
    private lateinit var tabs: TabLayout
    private var argsBinding: String? = null
    private lateinit var viewPager: ViewPager2

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
            changeTabLayoutIndicator();
        }

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            val view = layoutInflater.inflate(R.layout.custom_tab_layout, null)
            view.findViewById<ImageView>(R.id.icon).apply {
                setBackgroundResource(C_TAB_ICON[position])
                contentDescription = "Hello World"
            }
            tab.customView = view
        }.attach()

        initKeyboard()
    }


    private fun changeTabLayoutIndicator() {
        if (argsBinding == getString(R.string.args_binding_obj)) {
            tabs.getTabAt(1)?.select()
            viewPager.currentItem = 1
        }
        if (argsBinding == getString(R.string.args_binding_color)) {
            tabs.getTabAt(2)?.select()
            viewPager.currentItem = 2

        }
        if (argsBinding == getString(R.string.args_binding_currency)) {
            tabs.getTabAt(3)?.select()
            viewPager.currentItem = 3
        }
    }

    private fun initKeyboard() {
        //binding.btnKeyboard.setOnClickListener(::openKeyboardDialog)
    }

    private fun openKeyboardDialog(view: View) {
        val bottomSheetDialog = BottomSheetDialog(this).apply {
            setContentView(R.layout.custom_keyboard_dialog)
        }

        bottomSheetDialog.show()
    }

    companion object {
        @StringRes
        val C_TAB_ICON = intArrayOf(
            R.drawable.ic_bisindo_translator,
            R.drawable.ic_object_detection,
            R.drawable.ic_currency_detection,
            R.drawable.ic_text_detection,
            R.drawable.ic_currency_detection
        )

        // Need Content Desc


    }
}