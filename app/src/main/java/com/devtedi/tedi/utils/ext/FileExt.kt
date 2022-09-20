package com.devtedi.tedi.utils.ext

import android.net.Uri

val Uri.extension: String
    get() = this.toString().substringAfterLast('.', "")