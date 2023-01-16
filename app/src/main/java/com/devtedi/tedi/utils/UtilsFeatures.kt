package com.devtedi.tedi.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devtedi.tedi.R
import com.devtedi.tedi.interfaces.ObjectOptionInterface
import com.devtedi.tedi.interfaces.SignlanguageContentListener
import com.devtedi.tedi.utils.ext.click
import com.devtedi.tedi.utils.ext.gone
import com.devtedi.tedi.utils.ext.show
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer

fun sumTheDetectedCurrency(listOfCurrency : List<String>) : Double {
    var totalSum: Double = 0.0
    try {
        for (currency in listOfCurrency) {
            totalSum += currency.toDouble()
        }
    }
    catch (e : Exception) {
        totalSum = 100.0
    }
    return totalSum
}

fun makeItOneString(mappedItems : Map<String, Int>) : String {
    val stringBuilder = StringBuilder()
    for( content in mappedItems) {
        stringBuilder.append(content.value)
        stringBuilder.append(" ")
        stringBuilder.append(content.key)
        stringBuilder.append(",")
    }
    return stringBuilder.toString()
}

fun inputImageToBitmap(data : ByteBuffer, metadata : FrameMetadata) : Bitmap? {
    if (data != null) {
        data.rewind()
        val imageInBuffer: ByteArray = ByteArray(data.limit())
        data.get(imageInBuffer, 0, imageInBuffer.size)

        try {
            val yuvImage: YuvImage = YuvImage(
                imageInBuffer,
                ImageFormat.NV21,
                metadata.getWidth(),
                metadata.getHeight(),
                null
            )
            val byteOutputStream = ByteArrayOutputStream()
            // Create image, and Save the ByteArray to ByteOutputStream
            yuvImage.compressToJpeg(
                Rect(0, 0, metadata.getWidth(), metadata.getHeight()),
                80,
                byteOutputStream
            )

            val bitmapOutput = BitmapFactory.decodeByteArray(
                byteOutputStream.toByteArray(),
                0,
                byteOutputStream.size()
            )
            byteOutputStream.close()
            return bitmapOutput
        } catch (e: java.lang.Exception) {
            Log.e("VisionProcessesorBase", "ERROR : " + e.message)
        }
    }
    return null
}

fun createFile(activity : Activity) : File {
    val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
        File(it, "CameraThisable").apply {
            mkdirs()
        }
    }
    val outputDirectory = if(mediaDir!= null && mediaDir.exists()) mediaDir else activity.filesDir
    return File(outputDirectory, "Yoman.jpg")
}

fun countTheObj(list: List<String>) : MutableMap<String, Int> {
    val returned = mutableMapOf<String, Int>()
    for (content in list) {
        if (content in returned.keys) {
            returned[content] = returned[content]?.plus(1)!!
        }
        else {
            returned[content] = 1
        }
    }
    return returned
}
fun showAlertDialogSignLanguage(context : Context, signlanguageContentListener: SignlanguageContentListener? ) {
    val alertDialog  = AlertDialog.Builder(context)
        .setView(R.layout.custom_alertdialog_signlanguage)
        .setOnDismissListener {
            signlanguageContentListener?.onListenContent(false)
        }
        .create()

    alertDialog.show()
    val btnFinish = alertDialog.findViewById<Button>(R.id.btn_selesai)
    btnFinish.click {
        alertDialog.dismiss()
    }

}

/**
 * Fungsi untuk melakukan Scaling down pada input gambar
 * @param [bitmap] Gambar yang dilakukan scaling down
 * @param [maxDimension] Max dimension untuk di lakukan scaling down
 * @return Bitmap yang telah discaling !
 */
fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val originalWidth = bitmap.width
    val originalHeight = bitmap.height
    var resizedWidth = maxDimension
    var resizedHeight = maxDimension
    if (originalHeight > originalWidth) {
        resizedHeight = maxDimension
        resizedWidth =
            (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
    } else if (originalWidth > originalHeight) {
        resizedWidth = maxDimension
        resizedHeight =
            (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
    } else if (originalHeight == originalWidth) {
        resizedHeight = maxDimension
        resizedWidth = maxDimension
    }
    return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
}

/**
 * Fungsi untuk mendapatkan Nama Dari Device :
 * Model number of device, wkwk
 * @return Capitalize name Device model dan manufakturnya
 */

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        capitalize(model)
    } else {
        "${capitalize(model)} $model"
    }
}

/**
 * Fungsi untuk Kapitalisasi String
 * @param [s] String aja gan
 *
 */
private fun capitalize(s: String?): String {
    if (s == null || s.isEmpty()) {
        return ""
    }
    val first = s[0]
    return if (Character.isUpperCase(first)) {
        s
    } else {
        first.uppercaseChar().toString() + s.substring(1)
    }
}

fun getDeviceVersion(): String {
    return Build.VERSION.RELEASE
}

fun showLoading(dimmerView: View, progressBar: ProgressBar) {
    dimmerView.show()
    progressBar.show()
}

fun hideLoading(dimmerView: View, progressBar: ProgressBar) {
    dimmerView.gone()
    progressBar.gone()
}
