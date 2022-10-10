package com.devtedi.tedi.factory

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Build
import android.util.Log
import android.util.Size
import com.devtedi.tedi.observer_core.CoreObserver
import com.devtedi.tedi.observer_core.CoreSubject
import com.devtedi.tedi.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.nnapi.NnApiDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.CastOp
import org.tensorflow.lite.support.common.ops.DequantizeOp
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.common.ops.QuantizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

open class YOLOv5ModelCreator(
    input_size: Size,
    output_size: IntArray,
    detect_threshold: Float,
    IOU_threshold: Float,
    IOU_class_duplicated_threshold: Float,
    label_file: String,
    model_file: String,
    is_int_8: Boolean,

    ) : ModelTF(input_size, output_size,
    detect_threshold, IOU_threshold,
    IOU_class_duplicated_threshold, label_file,
    model_file, is_int_8), CoreSubject {

    private var observers  = arrayListOf<CoreObserver>()
    private var nmsRecognitionRes = ArrayList<RecognitionRes>()

    override suspend fun create(context: Context): YOLOv5ModelCreator {
        initialModel(context)
        return this
    }

    override fun addGpuDelegate() {
        val compatibilityList = CompatibilityList()
        if (compatibilityList.isDelegateSupportedOnThisDevice) {
            val delegateOptions = compatibilityList.bestOptionsForThisDevice
            val gpuDelegate = GpuDelegate(delegateOptions)
            options.addDelegate(gpuDelegate)
        } else {
            addThread(4)
            addNNApiDelegate()
        }
    }

    override fun addNNApiDelegate() {
        val nnApiDelegate: NnApiDelegate

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val nnApiOptions: NnApiDelegate.Options = NnApiDelegate.Options()
            nnApiOptions.allowFp16 = true
            nnApiOptions.executionPreference =
                NnApiDelegate.Options.EXECUTION_PREFERENCE_SUSTAINED_SPEED
            nnApiDelegate = NnApiDelegate(nnApiOptions)
            options.addDelegate(nnApiDelegate)
        }
    }

    override suspend fun initialModel(activity: Context) {
        Log.d("YOLOv5ModelCreator", Thread.currentThread().name)
        kotlin.runCatching {
            withContext(Dispatchers.IO) {
                val tfliteModel: ByteBuffer = FileUtil.loadMappedFile(activity, model_file)
                tflite = Interpreter(tfliteModel, options)
                associatedAxisLabels = FileUtil.loadLabels(activity, label_file)
            }
        }
    }

    override fun addThread(thread: Int) {
        options.numThreads = thread
    }

    override fun detect(bitmap: Bitmap): ArrayList<RecognitionRes> {

        var yolov5sTfliteInput: TensorImage
        val imageProcessor: ImageProcessor
        if (IS_INT_8) {
            imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(inputSize.height, inputSize.width, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0f, 255f))
                .add(
                    QuantizeOp(
                        input5SINT8QuantParams.zeroPoint.toFloat(),
                        input5SINT8QuantParams.scale
                    )
                )
                .add(CastOp(DataType.UINT8))
                .build()
            yolov5sTfliteInput = TensorImage(DataType.UINT8)
        } else {
            imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(inputSize.height, inputSize.width, ResizeOp.ResizeMethod.BILINEAR))
                .add(NormalizeOp(0f, 255f))
                .build()
            yolov5sTfliteInput = TensorImage(DataType.FLOAT32)
        }
        yolov5sTfliteInput.load(bitmap)
        yolov5sTfliteInput = imageProcessor.process(yolov5sTfliteInput)

        var probabilityBuffer: TensorBuffer
        probabilityBuffer = if (IS_INT_8) {
            TensorBuffer.createFixedSize(outputSize, DataType.UINT8)
        } else {
            TensorBuffer.createFixedSize(outputSize, DataType.FLOAT32)
        }

        tflite.run(yolov5sTfliteInput.buffer, probabilityBuffer.buffer)

        if (IS_INT_8) {
            val tensorProcessor = TensorProcessor.Builder()
                .add(
                    DequantizeOp(
                        output5SINT8QuantParams.zeroPoint.toFloat(),
                        output5SINT8QuantParams.scale
                    )
                )
                .build()
            probabilityBuffer = tensorProcessor.process(probabilityBuffer)
        }

        val recognitionArray = probabilityBuffer.floatArray
        val allRecognitions: ArrayList<RecognitionRes> =
            ArrayList<RecognitionRes>()
        for (i in 0 until outputSize[1]) {
            val gridStride = i * outputSize[2]
            val x = recognitionArray[0 + gridStride] * inputSize.width
            val y = recognitionArray[1 + gridStride] * inputSize.height
            val w = recognitionArray[2 + gridStride] * inputSize.width
            val h = recognitionArray[3 + gridStride] * inputSize.height
            val xmin = Math.max(0.0, x - w / 2.0).toInt()
            val ymin = Math.max(0.0, y - h / 2.0).toInt()
            val xmax = Math.min(inputSize.width.toDouble(), x + w / 2.0).toInt()
            val ymax = Math.min(inputSize.height.toDouble(), y + h / 2.0).toInt()
            val confidence = recognitionArray[4 + gridStride]
            val classScores =
                Arrays.copyOfRange(recognitionArray, 5 + gridStride, outputSize[2] + gridStride)

            var labelId = 0
            var maxLabelScores = 0f
            for (j in classScores.indices) {
                if (classScores[j] > maxLabelScores) {
                    maxLabelScores = classScores[j]
                    labelId = j
                }
            }
            val r = RecognitionRes(
                labelId,
                "",
                maxLabelScores,
                confidence,
                RectF(xmin.toFloat(), ymin.toFloat(), xmax.toFloat(), ymax.toFloat())
            )
            allRecognitions.add(
                r
            )
        }

        val nmsRecognitions: ArrayList<RecognitionRes> = nms(allRecognitions)
        val nmsFilterBoxDuplicationRecognitions: ArrayList<RecognitionRes> =
            nmsAllClass(nmsRecognitions)

        for (recognition in nmsFilterBoxDuplicationRecognitions) {
            val labelId: Int = recognition.getLabelId()
            val labelName = associatedAxisLabels[labelId]
            recognition.setLabelName(labelName)
        }
        nmsRecognitionRes = nmsFilterBoxDuplicationRecognitions
        notifyObserver()
        return nmsRecognitionRes

    }

    override fun close() {
        tflite.close()
    }

    private fun nms(allRecognitions: ArrayList<RecognitionRes>): ArrayList<RecognitionRes> {
        val nmsRecognitions: ArrayList<RecognitionRes> =
            ArrayList<RecognitionRes>()

        for (i in 0 until outputSize[2] - 5) {
            val pq: PriorityQueue<RecognitionRes> = PriorityQueue(
                6300
            ) { l, r -> // Intentionally reversed to put high confidence at the head of the queue.
                java.lang.Float.compare(r.getConfidence(), l.getConfidence())
            }

            for (j in allRecognitions.indices) {
                if (allRecognitions[j].getLabelId() == i && allRecognitions[j].getConfidence() > DETECT_THRESHOLD) {
                    pq.add(allRecognitions[j])
                }
            }

            while (pq.size > 0) {
                val a: Array<RecognitionRes?> = arrayOfNulls<RecognitionRes>(pq.size)
                val detections: Array<RecognitionRes> = pq.toArray(a)
                val max: RecognitionRes = detections[0]
                nmsRecognitions.add(max)
                pq.clear()
                for (k in 1 until detections.size) {
                    val detection: RecognitionRes = detections[k]
                    if (boxIou(max.getLocation(), detection.getLocation()) < IOU_THRESHOLD) {
                        pq.add(detection)
                    }
                }
            }
        }
        return nmsRecognitions
    }

    /**
     * 对所有数据不区分类别做非极大抑制
     *
     * @param allRecognitions
     * @return
     */
    private fun nmsAllClass(allRecognitions: ArrayList<RecognitionRes>): ArrayList<RecognitionRes> {
        val nmsRecognitions: ArrayList<RecognitionRes> =
            ArrayList<RecognitionRes>()
        val pq: PriorityQueue<RecognitionRes> = PriorityQueue(
            100
        ) { l, r -> // Intentionally reversed to put high confidence at the head of the queue.
            r.getConfidence().compareTo(l.getConfidence())
        }

        // 相同类别的过滤出来, 且obj要大于设定的阈值
        for (j in allRecognitions.indices) {
            if (allRecognitions[j].getConfidence() > DETECT_THRESHOLD) {
                pq.add(allRecognitions[j])
            }
        }
        while (pq.size > 0) {
            // 概率最大的先拿出来
            val a: Array<RecognitionRes?> = arrayOfNulls(pq.size)
            val detections: Array<RecognitionRes> = pq.toArray(a)
            val max: RecognitionRes = detections[0]
            nmsRecognitions.add(max)
            pq.clear()
            for (k in 1 until detections.size) {
                val detection: RecognitionRes = detections[k]
                val iou = boxIou(max.getLocation(), detection.getLocation())
                if (iou < IOU_CLASS_DUPLICATED_THRESHOLD) pq.add(detection)
            }
        }
        return nmsRecognitions
    }

    private fun boxIou(a: RectF, b: RectF): Float {
        val intersection = boxIntersection(a, b)
        val union = boxUnion(a, b)
        return if (union <= 0) 1f else intersection / union
    }

    private fun boxIntersection(a: RectF, b: RectF): Float {
        val maxLeft = max(a.left, b.left)
        val maxTop = max(a.top, b.top)
        val minRight = java.lang.Float.min(a.right, b.right)
        val minBottom = java.lang.Float.min(a.bottom, b.bottom)
        val w = minRight - maxLeft
        val h = minBottom - maxTop
        return if (w < 0 || h < 0) 0f else w * h
    }

    fun getResult() : ArrayList<RecognitionRes>
    {
        return this.nmsRecognitionRes
    }

    private fun boxUnion(a: RectF, b: RectF): Float {
        val i = boxIntersection(a, b)
        return (a.right - a.left) * (a.bottom - a.top) + (b.right - b.left) * (b.bottom - b.top) - i
    }

    override fun registerObserver(o: CoreObserver) {
        this.observers.add(o)
    }

    override fun removeObserver(o: CoreObserver) {
        this.observers.remove(o)
    }

    override fun notifyObserver() {

        for(obs in observers)
        {
            obs.update_observer()
        }
    }
}