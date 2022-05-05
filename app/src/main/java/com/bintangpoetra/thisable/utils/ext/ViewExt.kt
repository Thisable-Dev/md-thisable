package com.bintangpoetra.thisable.utils.ext

import android.view.View
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

fun ImageView.setImageUrl(url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}