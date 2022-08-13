package com.devtedi.tedi.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.mlkit.vision.objects.DetectedObject
import java.util.Locale

class ObjectGraphic(var graphicOverlay : GraphicOverlay, private var obj : DetectedObject): GraphicOverlay.Graphic(graphicOverlay){

    private lateinit var boxPaints : Array<Paint?>
    private lateinit var textPaints : Array<Paint?>
    private lateinit var labelPaint: Array<Paint?>

    init {
        //Initiate All Colors


        val numColors = 10
        textPaints = arrayOfNulls(numColors)
        boxPaints = arrayOfNulls(numColors)
        labelPaint = arrayOfNulls(numColors)

        for (i in 0 until numColors) {
            textPaints[i] = Paint()
            textPaints[i]?.color  = COLORS[i][0]
            textPaints[i]?.textSize = TEXT_SIZE

            boxPaints[i] = Paint()
            boxPaints[i]?.color = COLORS[i][1]
            boxPaints[i]?.style = Paint.Style.STROKE
            boxPaints[i]?.strokeWidth = STROKE_WIDTH

            labelPaint[i] = Paint()
            labelPaint[i]?.color = COLORS[i][1]
            labelPaint[i]?.style = Paint.Style.FILL
        }
    }

    override fun draw(canvas: Canvas?) {
        val colorID = 1
        var textWidth = textPaints[colorID]!!.measureText(obj.trackingId.toString())
        val lineHeight = TEXT_SIZE + STROKE_WIDTH
        var yLabelOffset = -lineHeight

        for (label in obj.labels) {
            textWidth = Math.max(textWidth, textPaints[colorID]!!.measureText(label.text)) // MeasureText tujuannya untuk Mengukur size dari label
            textWidth = Math.max(textWidth, textPaints[colorID]!!.measureText(String.format(Locale.US, LABEL_FORMAT, label.confidence * 100)))
            yLabelOffset -= 2 * lineHeight
        }

        val rect = RectF(obj.boundingBox)
        // Kalau misalnya gambarnya di Flupped, maka ubah, RectF bbox kiri jadi kanan, dan kanan jadi kiri
        val x0 = translateX(rect.left)
        val x1 = translateX(rect.right)
        // Draw BBOX

        rect.left = Math.min(x0, x1)
        rect.right = Math.max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)

        canvas?.drawRect(rect, boxPaints[2]!!)
        // Draw Other Object infO ( Buat bungkus informasi informasi terkait Label dkk )
        canvas?.drawRect(
            rect.left - STROKE_WIDTH,
            rect.top + yLabelOffset,
            rect.left + textWidth + 2 * STROKE_WIDTH,
            rect.top,
            labelPaint[colorID]!!
        )
        yLabelOffset += TEXT_SIZE

        // draw The Text ("
        //canvas?.drawText("",rect.left, rect.top + yLabelOffset, textPaints[colorID]!!)
        //yLabelOffset += lineHeight
        for (label in obj.labels) {
            // Draw Label
            val labelWhite = label.text.filter { !it.isWhitespace() }
            var label_fin = mapTheLabel(labelWhite)
            if(label_fin != null) {
                canvas?.drawText(
                    label_fin,
                    rect.left,
                    rect.top + yLabelOffset,
                    textPaints[colorID]!!
                )
                //more spacing
                yLabelOffset += lineHeight
                // Draw Confidence
                val stringConfidence =
                    String.format(Locale.US, LABEL_FORMAT, label.confidence * 100)
                canvas?.drawText(
                    stringConfidence,
                    rect.left,
                    rect.top + yLabelOffset,
                    textPaints[colorID]!!
                )
                //spacing
                yLabelOffset += lineHeight
            }
        }
    }
    private fun mapTheLabel(label : String) : String? {
        when (label) {
            "cat" -> return  "kucing"
            "dog" -> return  "anjing"
            "fruit" -> return  "buah"
            "motorbike" -> return "motor"
            "flower" -> return "bunga"
            "car" -> return "mobil"
        }
        return label
    }
    companion object {
        private const val TEXT_SIZE = 24.0F
        private const val STROKE_WIDTH = 4.0f // untuk Garis
        private const val NUM_COLORS = 10
        // ArrayOfColors ( 0 -> Text, 1 BBox )
        private val COLORS = arrayOf(
            intArrayOf(Color.BLUE, Color.BLUE),
            intArrayOf(Color.WHITE, Color.BLUE),
            intArrayOf(
                Color.BLACK, Color.BLUE
            ),
            intArrayOf(Color.WHITE, Color.RED),
            intArrayOf(Color.WHITE, Color.BLUE),
            intArrayOf(
                Color.WHITE, Color.DKGRAY
            ),
            intArrayOf(Color.BLACK, Color.CYAN),
            intArrayOf(Color.BLACK, Color.YELLOW),
            intArrayOf(
                Color.WHITE, Color.BLACK
            ),
            intArrayOf(Color.BLACK, Color.GREEN)
        )

        private const val LABEL_FORMAT = "%.2f%% confidence "
    }

}
