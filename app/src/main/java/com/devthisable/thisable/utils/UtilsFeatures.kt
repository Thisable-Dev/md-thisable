package com.devthisable.thisable.utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devthisable.thisable.R
import com.devthisable.thisable.adapter.ObjectOptionAdapter
import com.devthisable.thisable.interfaces.ObjectOptionInterface
import com.devthisable.thisable.interfaces.SignlanguageContentListener
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

fun showToastMessage(context : Context, msg : String) {
    Toast.makeText(context ,msg, Toast.LENGTH_SHORT).show()
}

fun showAlertDialogObjDetection (context : Context, contentDialog : Array<String >, subscriberItemListener : ObjectOptionInterface) {
    val dialog : Dialog = Dialog(context)
    val adapter = ObjectOptionAdapter(contentDialog)
    adapter.setOnClickItemListener(subscriberItemListener)
    dialog.setContentView(R.layout.custom_dialog_object)
    val layoutManager = LinearLayoutManager(context)
    val recyclerView = dialog.findViewById(R.id.dialog_rv) as RecyclerView
    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = adapter
    dialog.show()
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
    // Media/camerathisable/ ....YOMAN.jpg
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
    val btnSelesai = alertDialog.findViewById<Button>(R.id.btn_selesai)
    btnSelesai.setOnClickListener {
        alertDialog.dismiss()
    }


}

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
