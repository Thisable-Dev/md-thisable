package com.devtedi.tedi.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devtedi.tedi.utils.image_utility.YuvToRgbConverter

class ColorDetectionAnalyzer(private val context : Context) : ImageAnalysis.Analyzer{
    private lateinit var currImage : Bitmap

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val targetImage = image.image
        if(targetImage != null)
        {
            val yuvToRgbConverter = YuvToRgbConverter(context)
            val tempBitmap = Bitmap.createBitmap(targetImage.width, targetImage.height, Bitmap.Config.ARGB_8888)
            yuvToRgbConverter.yuvToRgb(targetImage, tempBitmap)
            currImage = tempBitmap
        }
        image.close()

    }

    @Synchronized
     fun getDetectedImage() : Bitmap
    {
        return currImage
    }
}