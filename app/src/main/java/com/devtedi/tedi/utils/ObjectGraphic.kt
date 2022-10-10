package com.devtedi.tedi.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.devtedi.tedi.observer_core.CoreObserver
import com.google.mlkit.vision.objects.DetectedObject
import java.util.Locale



class ObjectGraphic(var graphic: GraphicOverlay, private var objs: RecognitionRes) : GraphicOverlay.Graphic(graphic)
{
    private var boxPaints  = Array(NUM_COLORS){Paint()}
    private var textPaints =  Array(NUM_COLORS){Paint()}
    private var labelPaints = Array(NUM_COLORS){Paint()}
    private val observers  = arrayListOf<CoreObserver>();

    init {

        for (i in 0 until NUM_COLORS)
        {
            textPaints[i] = Paint()
            textPaints[i].color = COLORS[i][0]
            textPaints[i].textSize = TEXT_SIZE

            boxPaints[i] = Paint()
            boxPaints[i].color = COLORS[i][1]
            boxPaints[i].style = Paint.Style.STROKE
            boxPaints[i].strokeWidth = STROKE_WIDTH

            labelPaints[i] = Paint()
            labelPaints[i].color = COLORS[i][1]
            labelPaints[i].style = Paint.Style.FILL
        }
    }

    override fun draw(canvas: Canvas?)
    {
        val colorID = Math.abs(objs.getLabelId() % NUM_COLORS)

        var textWidth = textPaints[colorID].measureText("ID : " + objs.getLabelId())
        val lineHeight = TEXT_SIZE + STROKE_WIDTH
        var yLabelOffset = -lineHeight

        // Ngitung lebar dan tinggi dari label box
        textWidth = Math.max(textWidth, textPaints[colorID].measureText(
            String.format(
                Locale.US,
                LABEL_FORMAT,
                objs.getConfidence() * 100,
                1
            )
        ))

        yLabelOffset -= 2 * lineHeight

        val rect = RectF(objs.getLocation())
        val x0 = translateX(rect.left)
        val x1 = translateY(rect.right)

        rect.left = Math.min(x0, x1)
        rect.right = Math.max(x0, x1)
        rect.top = translateY(rect.top)
        rect.bottom = translateY(rect.bottom)
        canvas?.drawRect(rect, boxPaints[colorID])

        // Draw miscelenaous
        canvas?.drawRect(
            rect.left - STROKE_WIDTH,
            rect.top + yLabelOffset,
            rect.left + textWidth + 2 * STROKE_WIDTH,
            rect.top,
            labelPaints[colorID])

        yLabelOffset += lineHeight
        canvas?.drawText(
            "Label: " + objs.getLabelName(),
            rect.left,
            rect.top + yLabelOffset,
            textPaints[colorID]
        )

        yLabelOffset += lineHeight
        canvas?.drawText(
            String.format(
                Locale.US,
                LABEL_FORMAT,
                objs.getConfidence() * 100,
                1
            ),
            rect.left,
            rect.top + yLabelOffset,
            textPaints[colorID]
        )
        yLabelOffset += lineHeight
    }


    companion object
    {
        private const val TEXT_SIZE : Float = 24.0f
        private const val STROKE_WIDTH : Float = 4.0f
        private const val NUM_COLORS = 10
        private val COLORS =  arrayOf(
            intArrayOf(Color.BLACK, Color.WHITE),
            intArrayOf(Color.WHITE, Color.MAGENTA),
            intArrayOf(Color.BLACK, Color.LTGRAY),
            intArrayOf(Color.WHITE, Color.RED),
            intArrayOf(Color.WHITE, Color.BLUE),
            intArrayOf(Color.WHITE, Color.DKGRAY),
            intArrayOf(Color.BLACK, Color.CYAN),
            intArrayOf(Color.BLACK, Color.YELLOW),
            intArrayOf(Color.WHITE, Color.BLACK),
            intArrayOf(Color.BLACK, Color.GREEN)
        )

        private const val LABEL_FORMAT = "%.2f%% confidence(index : %d)"
    }


}
