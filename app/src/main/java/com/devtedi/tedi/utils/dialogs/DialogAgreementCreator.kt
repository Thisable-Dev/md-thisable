package com.devtedi.tedi.utils.dialogs

import android.app.AlertDialog
import android.content.Context

object DialogAgreementCreator {

    fun getDialog(
        context: Context, title: String,
        message: String, positiveMessage: String, negativeMessage: String,
        positiveHandler:() -> Unit , negativeHandler: () -> Unit) : AlertDialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveMessage) {_, _ ->
            positiveHandler()
        }
        .setNegativeButton(negativeMessage) {_, _ ->
            negativeHandler()
        }
        .create()
}