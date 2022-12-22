package com.devtedi.tedi.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devtedi.tedi.utils.image_utility.YuvToRgbConverter

class ColorDetectionAnalyzer(private val context : Context) : ImageAnalysis.Analyzer{
    /*
        Kelas yang implement interface Analyzer, bisa cek dokumentasinya
        Inti dari kelas ini adalah Provide Gambar dengan format yang sesuai
        untuk dapat bekerja pada Model nantinya
        kelas digunakan untuk ColorDetection
     */

    private lateinit var currImage : Bitmap

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        /*
            Simplenya Ini cuman ngambil image Data saja lalu di konversi menjadi Rgb
            karena pada dasarnya image yang diprovide masih dalam bentuk Yuv
            YUV ? Cari sendiri
         */
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