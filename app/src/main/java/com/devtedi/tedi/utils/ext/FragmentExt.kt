package com.devtedi.tedi.utils.ext

import androidx.fragment.app.Fragment
import com.devtedi.tedi.presentation.view.ContactBottomSheetFragment

fun Fragment.showDataBottomSheet(
    listener:((String) -> Unit)?) {
    var showBottomSheetDialog = childFragmentManager.findFragmentByTag(ContactBottomSheetFragment.javaClass.simpleName) as ContactBottomSheetFragment?
    if (showBottomSheetDialog == null) {
        showBottomSheetDialog = ContactBottomSheetFragment.newInstance(listener)
    }
    showBottomSheetDialog.isCancelable = true
    showBottomSheetDialog.show(childFragmentManager, ContactBottomSheetFragment.javaClass.simpleName)
}