package com.devtedi.tedi.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.devtedi.tedi.R
import com.devtedi.tedi.utils.ext.gone
import com.devtedi.tedi.utils.ext.show
import com.google.android.material.chip.Chip

class TediMainMenuButton(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs)  {

    init {
        inflate(context, R.layout.tedi_main_menu_button, this)

        val customAttributeStyle = context.obtainStyledAttributes(
            attrs,
            R.styleable.TediMainMenuButton,
            0, 0
        )

        val imgMenuIcon = findViewById<ImageView>(R.id.imgMenuIcon)
        val tvMenuTitle = findViewById<TextView>(R.id.tvMenuTitle)
        val chipBeta = findViewById<TextView>(R.id.tvBeta)

        try {
            imgMenuIcon.setImageDrawable(customAttributeStyle.getDrawable(R.styleable.TediMainMenuButton_iconMenu))
            tvMenuTitle.text = customAttributeStyle.getString(R.styleable.TediMainMenuButton_titleMenu)

            val isBeta = customAttributeStyle.getBoolean(R.styleable.TediMainMenuButton_isBeta, false)
            if (isBeta) {
                chipBeta.show()
            } else {
                chipBeta.gone()
            }
        } finally {
            invalidate()
            customAttributeStyle.recycle()
        }
    }

}