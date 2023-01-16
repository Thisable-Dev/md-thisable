package com.devtedi.tedi.analyzer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.devtedi.tedi.utils.image_utility.YuvToRgbConverter
/**
 *
    Kelas yang implement interface Analyzer, bisa cek dokumentasinya
    Inti dari kelas ini adalah Provide Gambar dengan format yang sesuai
    untuk dapat bekerja pada Model nantinya
    kelas digunakan untuk TextDetection
 *
 * */
class TextDetectionAnalyzer(private val context: Context) : ImageAnalysis.Analyzer {

    /*
     */
    private lateinit var currImage : Bitmap
    /*
    *
     Simplenya fungsi Ini cuman ngambil image Data saja lalu di konversi menjadi Rgb
     karena pada dasarnya image yang diprovide masih dalam bentuk Yuv
     YUV ? Cari sendiri
    *  */
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
