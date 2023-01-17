package com.devtedi.tedi.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Size
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import java.io.File

/**
 *
 * Abstraksi Untuk ModelTF, Ini di implement oleh YOLOv5ModelCreator
 * Itu yang di factory method, bisa pake abstraksinya aja biar lebih mudah untuk di extend dan digunakan sih
 * @property inputSize inputSizeModelObjectDetector
 * @property outputSize outputSizeModelObjectDetectorType
 * @property detect_threshold detectThreshold untuk objek yang terdeteksi
 * @property IOU_threshold IOU_Threshold untuk kalkulasi NMS
 * @property IOU_class_duplicated_threshold DUPLICATED_threshold untuk memdapatkan satu nilai terbaik untuk setiap label
 * @property label_file Lokasi dari label model
 * @property model_file Lokasi dari model file
 * @property IS_INT_8 Apakah model menggunakan .. Ahh lupa
 * @property tflite Tensorflow Interpreter ( Kek model )
 * @property associatedAxisLabels Label dari model
 * @property options Options untuk Tensorflow Interpreter
 * @property input5SINT8QuantParams Nilai Kuantisasi params, khusus dipake untuk model yang dikuantisasi
 * @property output5SINT8QuantParams Nilai Kuantisasi params, khusus dipake untuk model yang dikuantisasi
 */
abstract class ModelTF(

    var inputSize: Size,
    var outputSize: IntArray,
    protected var detect_threshold : Float,
    protected var IOU_threshold : Float,
    protected var IOU_class_duplicated_threshold : Float,
    protected var label_file : String,
    protected var model_file: File,
    protected var IS_INT_8: Boolean = false
) {

    abstract var tflite : Interpreter?
    abstract var associatedAxisLabels : List<String>?
    protected var input5SINT8QuantParams: Tensor.QuantizationParams =
        Tensor.QuantizationParams(0.003921568859368563f, 0)
    protected var output5SINT8QuantParams: Tensor.QuantizationParams =
        Tensor.QuantizationParams(0.006305381190031767f, 5)

    protected var options = Interpreter.Options()

    /**
     * Fungsi untuk membuat Model YOLOv5Model
     * @params [context] -> Context dari current activity/ fragment
     * @return YOLOv5ModelCreator model yang sudah memiliki interepter dan juga label
     */
    abstract suspend fun create(context : Context) : YOLOv5ModelCreator

    /**
     * Untuk menambahkan GPU Delegate, if any, but belum stabil, jadi belum digunakan
     */
    abstract fun addGpuDelegate()

    /**
     * Fungsi untuk Menambahkan NNAPI, untuk mempercepat komputasi
     */
    abstract fun addNNApiDelegate()

    /**
     * Fungsi untuk inisiasi model, membaca model file dan juga label file
     */
    abstract suspend fun initialModel(activity : Context)

    abstract fun addThread(thread : Int)

    /**
     * Fungsi untuk melakukan deteksi, pada bitmap
     * @param [bitmap] gambar yang hendak dilakukan deteksi
     * @return ArrayList hasil deteksi yang dihasilkan oleh model
     */
    abstract fun detect(bitmap : Bitmap) : ArrayList<RecognitionRes>

    abstract fun close()
}