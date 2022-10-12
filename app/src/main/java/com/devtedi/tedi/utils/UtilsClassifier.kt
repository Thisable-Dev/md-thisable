package com.devtedi.tedi.utils

import android.content.res.AssetManager
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

object UtilsClassifier
{
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

    @JvmStatic
    fun calculateOutputSize(input_size : Int, total_label : Int)  : IntArray
    {
        // inputSize / 2^n
        val inputSizeDiv_8 = input_size shr 3
        val inputSizeDiv_16 = input_size shr 4
        val inputSizeDiv_32 = input_size shr 5

        val value_toStore = (inputSizeDiv_8 * inputSizeDiv_8) + (inputSizeDiv_16 * inputSizeDiv_16) + (inputSizeDiv_32 * inputSizeDiv_32)
        val finTemp = value_toStore * 3
        return intArrayOf(1, finTemp, total_label + 5)
    }
}