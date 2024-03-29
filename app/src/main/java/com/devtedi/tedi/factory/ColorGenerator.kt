package com.devtedi.tedi.factory

import android.app.Activity
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.devtedi.tedi.R
import com.devtedi.tedi.databinding.CustomToastV1Binding
import com.devtedi.tedi.utils.ext.showCustomToast
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

/**
 * Kelas Color Generator bertugas untuk melakukan operasi operasi pada gambar untuk ekstraksi warnanya
 *
 * */
class ColorGenerator(private val context : Activity, private val inputImage : Bitmap) : ColorStore(context){
    /**
     * Step 1 : Konversi dlu bitmap kedalam bentuk MAT
     * Step 2 : Ini basically konversi ke HSV color format
     * Step 3 : Buat boundary color, Upper dan lowernya
     * Step 4 : Dilatasi warnanya
     * Step 5 : Lakukan Bitwise operation untuk mengetahui warna merah
     * Step 6 : Check Warna pada Input image
     */
    init {
        Utils.bitmapToMat(inputImage, inputImageMat)
        Imgproc.cvtColor(inputImageMat, hsvFrame, Imgproc.COLOR_BGR2HSV)
        iniateBoundaryColor()
        dilateColor()
        detectColorByBitwise()
        checkColorImage()
    }

    override fun dilateColor() {
        super.dilateColor()
    }

    override fun doContour(maskColor : Mat, colorName : String )  {
        /***
         * Basically Hanya melakukan Contour biasa aja sih
         */
        val contours : MutableList<MatOfPoint> = mutableListOf()
        var bboxContour : Rect = Rect()
        val listOfAreaMaps : MutableMap<Double, String> = mutableMapOf()

        Imgproc.cvtColor(maskColor, maskColor, Imgproc.COLOR_BGR2GRAY)
        Imgproc.findContours(
            maskColor,
            contours,
            hsvFrame,
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        for(c in contours)
        {
            val area : Double = Imgproc.contourArea(c)
            if(area >= thresholdArea )
            {
                bboxContour = Imgproc.boundingRect(c)

                val bbox_x = bboxContour.x.toDouble()
                val bbox_y = bboxContour.y.toDouble()
                val bbox_w = bboxContour.width.toDouble()
                val bbox_h = bboxContour.height.toDouble()

                Imgproc.rectangle(
                    inputImageMat,
                    Point(bbox_x, bbox_y),
                    Point(bbox_x + bbox_w, bbox_y + bbox_h),
                   defaultColor , 2
                )

                Imgproc.putText(
                    inputImageMat,
                    colorName,
                    Point(bbox_x, bbox_y),
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    2.0,
                    defaultColor
                )
                listOfAreaMaps[area] = colorName
                //addColor(colorName)
            }
        }
        if(listOfAreaMaps.isNotEmpty())
            findMaximum(listOfAreaMaps)
    }

    private fun findMaximum(listOfAreaMaps : MutableMap<Double, String>)
    {
        /**
         * Find the first and second primary color yang terbesar pada sebuah gambar
         * @param listOfAreaMaps -> pada dasarnya itu hanya Input dari contour yang terdeteksi oleh algoritma openCV saja
         * */
        val sortedKeys = listOfAreaMaps.keys.sortedDescending()
        if(sortedKeys.size == 1) {
            val maximunOne  : Double = sortedKeys[0]
            addColor(listOfAreaMaps[maximunOne] as String)
        }
        else
        {
            val maximunOne: Double = sortedKeys[0]
            val maximumTwo: Double= sortedKeys[1]
            addColor(listOfAreaMaps[maximunOne] as String)
            addColor(listOfAreaMaps[maximumTwo] as String)
        }
    }

    private fun checkColorImage()
    {
        /**
         *  Melakukan pengencekan warna pada input gambar saja,
         *
         * */
        var i = 0
        for (mask in arrOfResultColors)
        {
            doContour(mask, colorName = arrOfColors[i] )
            i ++
        }

        val bitmap = Bitmap.createBitmap(inputImageMat.cols(), inputImageMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(inputImageMat, bitmap)
        if(mUniqueColor.isNotEmpty())
            showResult(mUniqueColor.toList())
    }

    private fun showResult(stringResult : List<String>) {
        /**
         * Memberikan hasil warna yang terdeteksi pada user
         * @param stringResult list dari warna yang terdeteksi
         * */
        if (stringResult.isEmpty()) {
            context.showCustomToast("Tidak ada warna yang terdeteksi")
            return
        } else {
            val stringBuilder: StringBuilder = StringBuilder()
            stringBuilder.append(
                context.getString(R.string.response_1_color_detection)
            )
            stringBuilder.append(" ")
            if(stringResult.size == 1)
            {
                stringBuilder.append(stringResult[0])
            }
            else
            {
                for (indx in 1 until stringResult.size) {
                    if (indx == 1) {
                        stringBuilder.append(stringResult[0])
                        stringBuilder.append(", ")
                        stringBuilder.append(stringResult[indx])
                    } else if (stringResult.size == 2) {
                        stringBuilder.append(stringResult[indx])
                    }
                }
            }
            context.showCustomToast(stringBuilder.toString())
            mUniqueColor.clear()
        }

    }
    fun addColor(colorName : String)
    {
        mUniqueColor.add(colorName)
    }
    companion object {
        private const val thresholdArea = 500.0
        val defaultColor = Scalar(255.0,255.0,255.0)

    }
}