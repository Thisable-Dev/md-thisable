package com.devthisable.thisable.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devthisable.thisable.utils.image_utility.YuvToRgbConverter

class TextDetectionAnalyzer(private val context: Context) : ImageAnalysis.Analyzer {

    private lateinit var currImage : Bitmap
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val desiredImage = image.image
        if (desiredImage != null) {

            val yuvToRgbConverter = YuvToRgbConverter(context)
            val tempBitmap = Bitmap.createBitmap(desiredImage.width, desiredImage.height, Bitmap.Config.ARGB_8888)
            yuvToRgbConverter.yuvToRgb(desiredImage, tempBitmap)
            currImage = tempBitmap

        }
        image.close()
    }

    @Synchronized
    fun getDetectedImage() : Bitmap? {

        return currImage
    }




}
