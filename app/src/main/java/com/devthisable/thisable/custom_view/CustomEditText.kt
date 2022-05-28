package com.devthisable.thisable.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.devthisable.thisable.R
import com.devthisable.thisable.utils.ext.show
import org.w3c.dom.Text

class CustomEditText  : AppCompatEditText, View.OnTouchListener {
    private lateinit var clearButtonImage : Drawable

    constructor(context : Context) : super(context) {
        init()

    }

    constructor(context : Context, attrs:AttributeSet) : super(context, attrs) {
        init()

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        hint = "Terjemahan"
    }
    private fun init () {
        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_delete_24) as Drawable
        setOnTouchListener(this)

        addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) showClearButton()
                else hideClearButton()
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }


        })
    }

    private fun showClearButton() {
        setButtonDrawable(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawable()
    }

    private fun setButtonDrawable(
        startOfTheText : Drawable? = null,
        topOfTheText : Drawable? = null,
        endOfTheText : Drawable? = null,
        bottomOfTheText : Drawable? = null
    ) {

        // Set Location of the drawable with desired Location ( Not Null Value )
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )

    }
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        // Check if the drawables is not Null
        if (compoundDrawables[2] != null ) {

            val clearButtonStart  : Float
            val clearButtonEnd : Float

            var isClearButtonClicked = false

            //Defined the location
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth  + paddingStart).toFloat()
                when {
                    // Kalau lokasi eventnya
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            }
            else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when  {

                    event.x > clearButtonStart -> isClearButtonClicked = true
                }

            }

            if(isClearButtonClicked) {
                when (event.action) {
                    // If Ga di pencet
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_delete_24) as Drawable
                        showClearButton()
                        return true
                    }
                    // If dipencet
                    MotionEvent.ACTION_UP ->  {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_delete_24) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }

                    else -> return false
                }
            }
        }
        return false
    }

}