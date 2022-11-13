package com.devtedi.tedi.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.devtedi.tedi.R
import com.devtedi.tedi.interfaces.FeedbackSignLanguageListener
import com.devtedi.tedi.interfaces.observer_keyboard.KeyboardObserver
import com.devtedi.tedi.interfaces.observer_keyboard.KeyboardSubject
import okhttp3.internal.notify

class CustomEditText  : AppCompatEditText, View.OnTouchListener, KeyboardSubject {
    private lateinit var clearButtonImage : Drawable
    private lateinit var keyboardButtonImage : Drawable
    private var observers : ArrayList<KeyboardObserver> = ArrayList()
    private var stateKeyboard : Boolean = false
    private var stateClear : Boolean = false
    private var subscribeKeyboardSignLanguageListener : FeedbackSignLanguageListener? = null
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
        keyboardButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_24) as Drawable

        setOnTouchListener(this)
        showKeyboardButton()
        stateKeyboard = true
        addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    stateKeyboard = false
                    stateClear = true
                    showClearButton()
                }
                else
                {
                    hideClearButton()
                    stateKeyboard = true
                    stateClear = false
                    notifyObserver()
                    showKeyboardButton()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        })
    }

    private fun hideKeyboardButton() {
        setButtonDrawable()
    }
    private fun showKeyboardButton() {
        setButtonDrawable(endOfTheText = keyboardButtonImage)

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
                        keyboardButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_24) as Drawable
                        //showClearButton()
                        return true
                    }
                    // If dipencet
                    MotionEvent.ACTION_UP ->  {
                        clearButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_delete_24) as Drawable
                        keyboardButtonImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_keyboard_24) as Drawable
                        when {


                            text.isNullOrBlank() -> {
                                stateKeyboard = true
                            }

                            text != null ->
                            {
                                text?.clear()
                                stateKeyboard = false
                            }

                        }
                        if (stateKeyboard) {
                            showKeyboardButton()
                            subscribeKeyboardSignLanguageListener?.onListenerKeyboard(true)
                        }
                        else {
                            subscribeKeyboardSignLanguageListener?.onListenerKeyboard(false)
                            showKeyboardButton()
                        }
                        return true
                    }
                    else -> return false
                }
            }
        }
        return false
    }

    fun setKeyboardListener(keyboardSignLanguageListener : FeedbackSignLanguageListener) {
        this.subscribeKeyboardSignLanguageListener = keyboardSignLanguageListener
    }

    override fun registerObserver(o: KeyboardObserver) {
        if(!observers.contains(o))
        {
            observers.add(o)
        }
    }

    override fun removeObserver(o: KeyboardObserver) {
        if(observers.contains(o)) observers.remove(o)
    }

    override fun notifyObserver() {
        for( observer in observers)
        {
            observer.updateObserver()
        }
    }

}