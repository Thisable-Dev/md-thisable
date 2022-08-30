package com.devtedi.tedi.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.devtedi.tedi.R

class TediSettingButton(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    init {
        inflate(context, R.layout.tedi_setting_button, this)

        val customAttributeStyle = context.obtainStyledAttributes(
            attrs,
            R.styleable.TeDiSettingButton,
            0, 0
        )

        val imgMenuIcon = findViewById<ImageView>(R.id.imgIcon)
        val tvMenuTitle = findViewById<TextView>(R.id.tvMenuTitle)

        try {
            imgMenuIcon.setImageDrawable(customAttributeStyle.getDrawable(R.styleable.TeDiSettingButton_menuIcon))
            tvMenuTitle.text = customAttributeStyle.getString(R.styleable.TeDiSettingButton_menuTitle)
        } finally {
            invalidate()
            customAttributeStyle.recycle()
        }
    }

}