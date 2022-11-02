package com.devtedi.tedi.utils.ext

import android.app.Activity
import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.devtedi.tedi.R.string
import com.devtedi.tedi.databinding.CustomToastV1Binding
import com.devtedi.tedi.utils.ConstVal.TEXT_DETECTION_LABEL

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

fun Context.copyToClipBoard(text: String) {
    val clipBoardManager= getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText(TEXT_DETECTION_LABEL, text)
    clipBoardManager.setPrimaryClip(clipData)

    showToast(getString(string.label_text_copied))
}

fun Activity.showCustomToast(message: String) {
    val toastCustomLayout : CustomToastV1Binding = CustomToastV1Binding.inflate(this.layoutInflater)

    toastCustomLayout.textCustom.setText(message)

    val toast = Toast(this)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = toastCustomLayout.root
    toast.show()
}