package com.bintangpoetra.thisable.utils.ext

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context.showOKDialog(title: String, message: String) {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        setPositiveButton("OK") { p0, _ ->
            p0.dismiss()
        }
    }.create().show()
}