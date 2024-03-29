package com.devtedi.tedi.utils.dialogs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object DialogAgreementCreator {

    fun getDialog(
        context: Context, title: String,
        message: String, positiveMessage: String, negativeMessage: String,
        positiveHandler:() -> Unit , negativeHandler: (DialogInterface) -> Unit) : AlertDialog = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(positiveMessage) {_, _ ->
            positiveHandler()
        }
        .setNegativeButton(negativeMessage) { dialog , _ ->
            negativeHandler.invoke(dialog)
        }
        .setCancelable(false)
        .create()
}