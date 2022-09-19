package com.devtedi.tedi.utils.ext

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.bumptech.glide.Glide

fun View.gone() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.disable () {
    isEnabled = false
}

fun View.enable() {
    isEnabled = true
}

fun ImageView.setImageUrl(url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}

fun View.popTap(){
    this.visibility = View.VISIBLE
    this.alpha = 1.0f

    this.pivotX = (this.width / 2).toFloat()
    this.pivotY = (this.height / 2).toFloat()

    val scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", 0.7f)
    val scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", 0.7f)

    scaleDownX.duration = 100
    scaleDownY.duration = 100

    val scaleUpX = ObjectAnimator.ofFloat(this, "scaleX", 1.0f)
    val scaleUpY = ObjectAnimator.ofFloat(this, "scaleY", 1.0f)

    scaleUpX.duration = 100
    scaleUpY.duration = 100

    val scaleDown = AnimatorSet()
    scaleDown.play(scaleDownX).with(scaleDownY)
    scaleDown.start()

    val scaleUp = AnimatorSet()
    scaleUp.play(scaleUpX).with(scaleUpY).after(scaleDown)
    scaleUp.start()
}

fun EditText.clearText() {
    setText("")
}

fun EditText.onTextChanged(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onTextChanged.invoke(char.toString())
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    })
}

infix fun View.click(click: () -> Unit) {
    setOnClickListener { click() }
}