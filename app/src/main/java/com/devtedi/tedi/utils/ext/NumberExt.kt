package com.devtedi.tedi.utils.ext

fun String.isPhoneNumberValid() : Boolean {
    return if (this.isEmpty() || this.length < 6 || this.length > 13) {
        false
    } else {
        android.util.Patterns.PHONE.matcher(this).matches()
    }
}