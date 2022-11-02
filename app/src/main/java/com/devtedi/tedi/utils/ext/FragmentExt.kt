package com.devtedi.tedi.utils.ext

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.CustomToastV1Binding
import com.devtedi.tedi.presentation.view.ContactBottomSheetFragment
import com.devtedi.tedi.utils.dialogs.DialogAgreementCreator
import java.lang.StringBuilder

fun Fragment.showDataBottomSheet(
    listener: ((String) -> Unit)?
) {
    var showBottomSheetDialog =
        childFragmentManager.findFragmentByTag(ContactBottomSheetFragment.javaClass.simpleName) as ContactBottomSheetFragment?
    if (showBottomSheetDialog == null) {
        showBottomSheetDialog = ContactBottomSheetFragment.newInstance(listener)
    }
    showBottomSheetDialog.isCancelable = true
    showBottomSheetDialog.show(
        childFragmentManager,
        ContactBottomSheetFragment.javaClass.simpleName
    )
}

fun Fragment.getTotalMemories(): Int {
    val memInfo = ActivityManager.MemoryInfo()
    return memInfo.totalMem.toInt()
}

fun Fragment.showCustomToast(message: String) {
    val toastCustomLayout: CustomToastV1Binding = CustomToastV1Binding.inflate(this.layoutInflater)

    toastCustomLayout.textCustom.setText(message)

    val toast = Toast(this.context)
    toast.duration = Toast.LENGTH_SHORT
    toast.view = toastCustomLayout.root
    toast.show()
}

fun Fragment.showCustomDialog(
    title: String,
    message: String,
    positiveButton: String,
    negativeButton: String,
    onClickPositive: () -> Unit,
    onClickNegative: (DialogInterface) -> Unit,
) {
    DialogAgreementCreator.getDialog(requireContext(), title, message,
        positiveButton, negativeButton, onClickPositive, onClickNegative).show()
}