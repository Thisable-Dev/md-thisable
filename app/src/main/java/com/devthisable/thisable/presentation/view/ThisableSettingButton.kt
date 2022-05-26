package com.devthisable.thisable.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.devthisable.thisable.R

class ThisableSettingButton(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    init {
        inflate(context, R.layout.thisable_setting_button, this)

        val customAttributeStyle = context.obtainStyledAttributes(
            attrs,
            R.styleable.ThisableSettingButton,
            0, 0
        )

        val imgMenuIcon = findViewById<ImageView>(R.id.imgIcon)
        val tvMenuTitle = findViewById<TextView>(R.id.tvMenuTitle)

        try {
            imgMenuIcon.setImageDrawable(customAttributeStyle.getDrawable(R.styleable.ThisableSettingButton_menuIcon))
            tvMenuTitle.text = customAttributeStyle.getString(R.styleable.ThisableSettingButton_menuTitle)
        } finally {
            invalidate()
            customAttributeStyle.recycle()
        }
    }

}