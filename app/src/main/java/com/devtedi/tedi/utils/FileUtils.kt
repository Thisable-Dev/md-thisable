package com.devtedi.tedi.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.devtedi.tedi.utils.ext.extension
import com.devtedi.tedi.utils.ext.showToast
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun uriToFile(selectedImg: Uri, context: Context, uri: Uri): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createTempFile(context, uri)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun createTempFile(context: Context, uri: Uri): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val suffix = if (uri.extension == "pdf") "pdf" else "jpg"
    return File.createTempFile(timeStamp, ".$suffix", storageDir)
}