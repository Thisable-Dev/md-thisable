package com.devtedi.tedi.utils

import android.content.res.AssetManager
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

/**
 * Static Kelas untuk mendukung perhitungan pada model
 */
object UtilsClassifier
{
    /**
     * Fungsi untuk load model file pada assetManager, but sudah tidak digunakan lagi
     * @param [assets] AssetManager Objek [modelFilename] namamodel pada asset
     */
    @JvmStatic
    fun loadModelFile(assets : AssetManager, modelFilename : String ) : MappedByteBuffer
    {
        // Docs :
        // https://www.baeldung.com/java-filechannel
        val fileDescriptor = assets.openFd(modelFilename)
        val inputStream : FileInputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel : FileChannel =  inputStream.channel

        val startOffset : Long = fileDescriptor.startOffset
        val declaredLength : Long = fileDescriptor.declaredLength

        // Load Section of a file into memory
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     *
        Untuk ngitung Outputsize dari YOLOv5, ada di dokumentasi YOLOv5 nya, pada dasarnya kalau misalnya
        input image = 320 * 320 , maka hasilnya di finTemp itu yak
        tinggal
        (320 / 8 * 320 / 8 ) + (320 / 16 * 320 / 16)  + ( 320 / 32 * 320 * 32)
        ( 40 * 40 ) + ( 20 * 20 ) + ( 10 * 10 ) =
        1600 + 400 + 100 = 2100 * 3
        3 Buat RGB
        6300, jadi dia butuh 6300 outputsize
     *
     * @param [input_size] input_size dari model, [total_label] Total label yang disupport model
     * @return IntArray , 3Dimension array [ 1, outputsize , label + 5]
     */
    @JvmStatic
    fun calculateOutputSize(input_size : Int, total_label : Int)  : IntArray
    {
        /*
         */
        val inputSizeDiv_8 = input_size shr 3
        val inputSizeDiv_16 = input_size shr 4
        val inputSizeDiv_32 = input_size shr 5

        val value_toStore = (inputSizeDiv_8 * inputSizeDiv_8) + (inputSizeDiv_16 * inputSizeDiv_16) + (inputSizeDiv_32 * inputSizeDiv_32)
        val finTemp = value_toStore * 3
        return intArrayOf(1, finTemp, total_label + 5)
    }
}