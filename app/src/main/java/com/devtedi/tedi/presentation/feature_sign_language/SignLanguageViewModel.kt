package com.devtedi.tedi.presentation.feature_sign_language

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtedi.tedi.factory.ObjectDetectorStore
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.interfaces.observer_core.CoreObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.lang.StringBuilder

/**
 *
 * Kelas ini digunakan sebagai ViewModel dari SignLanguageFragment
 *
 */
class SignLanguageViewModel : ViewModel(), CoreObserver {

    // Instance yoloV5
    private val _yolov5TFLiteDetector : MutableLiveData<YOLOv5ModelCreator> = MutableLiveData()
    val yolov5TFLiteDetector : LiveData<YOLOv5ModelCreator> = _yolov5TFLiteDetector

    private val _tobeWrittenString : MutableLiveData<String> = MutableLiveData()
    val tobeWrittenString : LiveData<String> = _tobeWrittenString

    private var sentence : ArrayList<String> = ArrayList()
    private var wordList : ArrayList<String> = ArrayList()

    private val _isLoading : MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

    private fun getOutputValue() {

        val tempResult = _yolov5TFLiteDetector.value?.getResult()
        if(tempResult != null && tempResult.isNotEmpty())
        {
            wordList.add(tempResult[0].getLabelName())
            if(wordList.size > 1)
            {
                wordList.add(wordList[wordList.size - 1])
                wordList.removeFirst()
            }

            if(wordList.isNotEmpty())
            {
                if (sentence.size > 0)
                {
                    if (wordList[0] != sentence.last())
                    {
                        sentence.add(wordList[0])
                        _tobeWrittenString.value = makeStringOnly(sentence)
                    }
                }

                else
                {
                    sentence.add(wordList[0])
                    _tobeWrittenString.value = makeStringOnly(sentence)
                }
            }

            if (sentence.size > 10)
            {
                sentence = ArrayList()
            }
        }
        /*
            val tempResult = _yolov5TFLiteDetector.value?.getResult()
            if(tempResult != null && tempResult.isNotEmpty())
            {
                if(previousString == null)
                {
                    previousString = tempResult[0].getLabelName()
                    _tobeWrittenString.value = tempResult[0].getLabelName()
                }
                else {
                    val tempResultFirst = tempResult[0].getLabelName()
                    if(mapOfString.containsKey(tempResultFirst)) {
                        mapOfString[tempResultFirst] = (mapOfString[tempResultFirst] ?: 0 ) + 1
                        if(mapOfString.get(tempResultFirst)!! >= 10 && previousString != tempResultFirst) {
                            previousString  = tempResultFirst
                            _tobeWrittenString.value = tobeWrittenString.value + tempResultFirst
                            mapOfString.clear()
                        }
                    }
                    else {
                        mapOfString[tempResultFirst] = 1
                    }

                }
            }

         */
    }

    private fun makeStringOnly( arr : ArrayList<String>)  : String
    {
        val strBuilder = StringBuilder()
        arr.forEach {
            strBuilder.append(it)
            strBuilder.append(" ")
        }
        return strBuilder.toString()
    }

    fun removePredictionContent()
    {
        wordList.clear()
        sentence.clear()
        //_tobeWrittenString.value = "Cleared"
    }
    fun initModel(modelName : String, filePath : File,context : Context)
    {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val model = ObjectDetectorStore(context).createModel(modelName, filePath).apply {
                    addGpuDelegate()
                    initialModel(context)
                }

                _yolov5TFLiteDetector.postValue(model)
                _isLoading.postValue(false)
            }
            catch (e : Exception)
            {
                e.printStackTrace()
            }

        }

    }

    override fun update_observer() {
        Log.d("UpdateObserverTerpanggil","bang")
        getOutputValue()
    }

}