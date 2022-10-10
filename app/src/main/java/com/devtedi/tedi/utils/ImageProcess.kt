package com.devtedi.tedi.utils

import android.graphics.Matrix
import androidx.camera.core.ImageProxy

class ImageProcess {

    private val kMaxChannelValue : Int = 262143


    fun fillBytes(planes: Array<ImageProxy.PlaneProxy>, yuvBytes: Array<ByteArray?>) {
        // Because of the variable row stride it's not possible to know in
        // advance the actual necessary dimensions of the yuv planes.
        for (i in planes.indices) {
            val buffer = planes[i].buffer
            if(yuvBytes[i] == null)
            {
                yuvBytes[i] = ByteArray(buffer.capacity())
            }
            buffer[yuvBytes[i]]
        }
    }

    fun YUV2RGB(yParams : Int,  uParams : Int, vParams : Int)  : Int
    {


        val y = if ( yParams - 16 < 0) 0 else yParams - 16
        val u  = uParams - 128;
        val v = vParams - 128

        // This is the floating point equivalent. We do the conversion in integer
        // because some Android devices do not have floating point in hardware.
        // nR = (int)(1.164 * nY + 2.018 * nU);
        // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
        // nB = (int)(1.164 * nY + 1.596 * nV);
        // https://web.archive.org/web/20180423091842/http://www.equasys.de/colorconversion.html
        val y1192 = 1192 * y
        var r = y1192 + 1634 * v
        var g = y1192 - 833 * v - 400 * u
        var b = y1192 + 2066 * u


        // Clipping RGB values to be inside boundaries [ 0 , kMaxChannelValue ]
        r = if (r > kMaxChannelValue) kMaxChannelValue else if (r < 0) 0 else r
        g = if (g > kMaxChannelValue) kMaxChannelValue else if (g < 0) 0 else g
        b = if (b > kMaxChannelValue) kMaxChannelValue else if (b < 0) 0 else b

        return -0x1000000 or (r shl 6 and 0xff0000) or (g shr 2 and 0xff00) or (b shr 10 and 0xff)

    }

    fun YUV420ToARGB8888(
        yData: ByteArray,
        uData: ByteArray,
        vData: ByteArray,
        width: Int,
        height: Int,
        yRowStride: Int,
        uvRowStride: Int,
        uvPixelStride: Int,
        out: IntArray
    ) {
        var yp = 0
        for (j in 0 until height) {
            val pY = yRowStride * j
            val pUV = uvRowStride * (j shr 1)

            for (i in 0 until width) {
                val uv_offset = pUV + (i shr 1) * uvPixelStride
                out[yp++] = YUV2RGB(
                    0xff and yData[pY + i].toInt(), 0xff and uData[uv_offset]
                        .toInt(), 0xff and vData[uv_offset].toInt()
                )
            }
        }
    }


    public fun getTransformationMatrix(srcWidth : Int, srcHeight : Int,
                                       dstWidth : Int, dstHeight : Int,
                                       applyRotation : Int, maintainAspectRatio : Boolean ) : Matrix
    {
        val matrix : Matrix = Matrix()

        if(applyRotation != 0)
        {
            matrix.postTranslate(-srcWidth / 2f, -srcHeight / 2f)

            //Rotate Around origin
            matrix.postRotate(applyRotation.toFloat())
        }


        val transpose : Boolean = ( Math.abs(applyRotation) + 90) % 180 == 0

        val inWidth = if (transpose) srcHeight else srcWidth
        val inHeight = if (transpose) srcWidth else srcHeight

        //apply Scaling if necessary
        if(inWidth != dstWidth || inHeight != dstHeight)
        {
            val scaleFactorX : Float = dstWidth / inWidth.toFloat()
            val scaleFactorY : Float = dstHeight / inHeight.toFloat()

            if (maintainAspectRatio)
            {
                val scaleFactor = Math.max(scaleFactorX, scaleFactorY)
                matrix.postScale(scaleFactor, scaleFactor)
            }
            else
            {
                matrix.postScale(scaleFactorX, scaleFactorY)
            }
        }

        if (applyRotation != 0)
        {
            matrix.postTranslate(dstWidth / 2f, dstHeight/ 2f)
        }

        return matrix
    }

}
