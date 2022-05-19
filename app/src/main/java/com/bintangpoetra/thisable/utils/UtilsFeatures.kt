package com.bintangpoetra.thisable.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bintangpoetra.thisable.R
import com.bintangpoetra.thisable.adapter.ObjectOptionAdapter
import com.bintangpoetra.thisable.interfaces.ObjectOptionInterface
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
        Log.d("HELLOW_WORLD", data.toString())
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
