package com.devtedi.tedi.factory

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class ColorGenerator(private val context : Context, private val inputImage : Bitmap) : ColorStore(context){

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

        val contours : MutableList<MatOfPoint> = mutableListOf()
        var bboxContour : Rect = Rect()

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
            val area = Imgproc.contourArea(c)
            if(area > thresholdArea )
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

                addColor(colorName)
            }
        }

    }

    fun checkColorImage()
    {
        var i = 0
        for (mask in arrOfResultColors)
        {
            doContour(mask, colorName = arrOfColors[i] )
            i ++
        }

        val bitmap = Bitmap.createBitmap(inputImageMat.cols(), inputImageMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(inputImageMat, bitmap)

        Toast.makeText(context, mUniqueColor.toString(), Toast.LENGTH_LONG).show()
    }

    fun addColor(colorName : String)
    {
        mUniqueColor.add(colorName)

    }
    companion object {
        private const val thresholdArea = 10.0
        val defaultColor = Scalar(255.0,255.0,255.0)

    }
}