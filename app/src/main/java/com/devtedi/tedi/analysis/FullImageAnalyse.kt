package com.devtedi.tedi.analysis

import android.content.Context
import android.graphics.*
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.interfaces.observer_analyzer.AnalyzerObserver
import com.devtedi.tedi.utils.*



/**
 *
 * Kelas ini digunakan sebagai Analyzer untuk inputan image dari preview view
 * Kelas ini implement
 *
 * @ ImageAnalysis.Analyzer -> Interface untuk analyzer untuk dilakukan custom processing pada fungsi analyze
 * @ AnalyzerObserver -> Observer untuk mendapatkan data ..... #TODO ADD The AnalyzerObserve Docs
 * @property context context pada fragment/ activity
 * @property previewView untuk display kameraX feed
 * @property yolov5TFLiteDetector untuk melakukan prediksi pada setiap frame
 * @property imageProcess untuk image preprocessing
 * @property graphicOverlay untuk menggambar bbox
 * @property onResult untuk handler click listener
 * @property onDetect untuk mendapatkan state dari AnalyzerSubject
 */

class FullImageAnalyse(
    val context: Context,
    private val previewView: PreviewView,
    private val rotation: Int,
    private val yolov5TFLiteDetector: YOLOv5ModelCreator,
    private val ImageProcess: ImageProcess = ImageProcess(),
    private val graphicOverlay: GraphicOverlay,
    private val onResult: (String) -> Unit
) : ImageAnalysis.Analyzer, AnalyzerObserver {

    private var onDetect: Boolean = true

    class Result(var costTime: Long, var bitmap: Bitmap)


    // DatabaseOfDetected Labels:

    private val soundPlayer = SoundPlayer.getInstance(context)

    /**
     * Remember : TRANSFORMASI Matrix itu hanya mapping dari a ke b secara simplenya, jadi gausah pusing
     * Fungsi ini digunakan untuk melakukan processing image disetiap frame dari previewView
     * Step 1 : Extract informasi dari setiap gambar dan bentuk pula yuvBytes kosong untuk menyimpan informasi yuv dari gambar
     * Step 2 : Masukan bytes dari gambar kepada yuvBytes
     * Step 3 : Konversikan YUV ke ARGB888 ( Basically Kotlin default format )
     * Step 4 : Bentuk imageBitmap kosong dan isi pikselnya dengan rgbBytes yang telah memiliki hasil konversi
     * Step 5 : Scale, ya untuk scaling gambar dari preview aja
     * Step 6 : fullScreenTransform : Melakukan transformasi dari current gambar size kepada Scaled gambar size, if any
     * Step 7 : Simpan Hasil transform pada fullImageBitmap, if ada scale yang pasti bakalan membesar/mengecil depends on scale
     * Step 8 : Crop imagenya menjadi inputan yang sesuai dengan input yolo model, dengan melakukan transformasi dari preview ke model matrix
     * Step 9 : Then draw the result dengan menggunakan informasi hasil prediksi
     * @param [image] Hanya image yang dengan YUV color
     *
     */

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
            previewWidth, previewHeight,
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
        if (onDetect) {
            val recognitions: ArrayList<RecognitionRes> =
                yolov5TFLiteDetector.detect(modelInputBitmap)
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
                var location: RectF = res.getLocation()
                modelToPreviewTransform.mapRect(location, location)
                res.setLocation(location)
                graphicOverlay.add(ObjectGraphic(this.graphicOverlay, res))
            }

            recognitions.toSet().forEach {
                onResult(it.getLabelName())
            }
        }

        val endTime = System.currentTimeMillis()
        val costTime = endTime - startTime

        image.close()
    }

    override fun updateObserver() {
        onDetect = !onDetect
    }

    private var lastPlayTime: Long = 0L

    companion object {
        private const val TIME_INTERVAL_WORD: Long = 500
        private const val TIME_INTERVAL_CLEAR: Long = 10
    }
}