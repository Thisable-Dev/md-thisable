package com.devtedi.tedi.analysis

import android.content.Context
import android.graphics.*
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.interfaces.observer_analyzer.AnalyzerObserver
import com.devtedi.tedi.interfaces.observer_analyzer.AnalyzerSubject
import com.devtedi.tedi.utils.GraphicOverlay
import com.devtedi.tedi.utils.ImageProcess
import com.devtedi.tedi.utils.ObjectGraphic
import com.devtedi.tedi.utils.RecognitionRes
import timber.log.Timber

class FullImageAnalyse(
    val context: Context,
    private val previewView: PreviewView,
    private val rotation: Int,
    private val yolov5TFLiteDetector: YOLOv5ModelCreator,
    private val ImageProcess: ImageProcess = ImageProcess(),
    private val graphicOverlay: GraphicOverlay
) : ImageAnalysis.Analyzer, AnalyzerObserver {

    private var onDetect : Boolean = true
    class Result(var costTime: Long, var bitmap: Bitmap)

    override fun analyze(image: ImageProxy) {

        val previewHeight = previewView.height
        val previewWidth = previewView.width
        val startTime = System.currentTimeMillis()
        val yuvBytes = arrayOfNulls<ByteArray>(3)
        val planes = image.planes
        val imageHeight = image.height
        val imageWidth = image.width

        // NOTE : HardCODED False
        graphicOverlay.setImageSourceInfo(imageWidth, imageHeight, false)
        ImageProcess.fillBytes(planes, yuvBytes)
        val yRowStride = planes[0].rowStride
        val uvRowStride = planes[1].rowStride
        val uvPixelStride = planes[1].pixelStride

        val rgbBytes = IntArray(imageHeight * imageWidth)
        ImageProcess.YUV420ToARGB8888(
            yuvBytes[0] as ByteArray,
            yuvBytes[1] as ByteArray,
            yuvBytes[2] as ByteArray,
            imageWidth,
            imageHeight,
            yRowStride,
            uvRowStride,
            uvPixelStride,
            rgbBytes
        )

        val imageBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        imageBitmap.setPixels(rgbBytes, 0, imageWidth, 0, 0, imageWidth, imageHeight)

        val scale: Double = java.lang.Double.max(
            previewHeight / (if (rotation % 180 == 0) imageWidth else imageHeight).toDouble(),
            previewWidth / (if (rotation % 180 == 0) imageHeight else imageWidth).toDouble()
        )
        val fullScreenTransform: Matrix = ImageProcess.getTransformationMatrix(
            imageWidth, imageHeight, (scale * imageHeight).toInt(), (scale * imageWidth).toInt(),
            if (rotation % 180 == 0) 90 else 0, false
        )

        val fullImageBitmap = Bitmap.createBitmap(
            imageBitmap,
            0,
            0,
            imageWidth,
            imageHeight,
            fullScreenTransform,
            false
        )

        val cropImageBitmap =
            Bitmap.createBitmap(fullImageBitmap, 0, 0, previewWidth, previewHeight)

        val previewToModelTransform: Matrix = ImageProcess.getTransformationMatrix(
            cropImageBitmap.width, cropImageBitmap.height,
            yolov5TFLiteDetector.inputSize.width,
            yolov5TFLiteDetector.inputSize.height,
            0, false
        )
        val modelInputBitmap = Bitmap.createBitmap(
            cropImageBitmap, 0, 0,
            cropImageBitmap.width, cropImageBitmap.height,
            previewToModelTransform, false
        )

        val modelToPreviewTransform = Matrix()
        previewToModelTransform.invert(modelToPreviewTransform)
        if(onDetect) {
            val recognitions: ArrayList<RecognitionRes> =
                yolov5TFLiteDetector.detect(modelInputBitmap)
            val emptyCropSizeBitmap =
                Bitmap.createBitmap(previewWidth, previewHeight, Bitmap.Config.ARGB_8888)
            val cropCanvas = Canvas(emptyCropSizeBitmap)

            val boxPaint = Paint()
            boxPaint.strokeWidth = 5F
            boxPaint.style = Paint.Style.STROKE
            boxPaint.color = Color.RED

            val textPain = Paint()
            textPain.textSize = 50F
            textPain.color = Color.RED
            textPain.style = Paint.Style.FILL

            graphicOverlay.clear()
            for (res in recognitions) {
                graphicOverlay.add(ObjectGraphic(this.graphicOverlay, res))
                val location: RectF = res.getLocation()
                val label: String = res.getLabelName()
                val confidence: Float = res.getConfidence()
                modelToPreviewTransform.mapRect(location)
                cropCanvas.drawRect(location, boxPaint)
                cropCanvas.drawText(
                    label + ":" + String.format("%.2f", confidence),
                    location.left,
                    location.top,
                    textPain
                )
            }
        }

        val endTime = System.currentTimeMillis()
        val costTime = endTime - startTime

        image.close()


    }

    override fun updateObserver() {
        if(onDetect) onDetect = false
        else onDetect = true

    }


}