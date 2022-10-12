package com.devtedi.tedi.presentation.feature_sign_language

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtedi.tedi.factory.ObjectDetectorStore
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import com.devtedi.tedi.observer_core.CoreObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignLanguageViewModel : ViewModel(), CoreObserver {

    private val _yolov5TFLiteDetector : MutableLiveData<YOLOv5ModelCreator> = MutableLiveData()
    val yolov5TFLiteDetector : LiveData<YOLOv5ModelCreator> = _yolov5TFLiteDetector


    private val _tobeWrittenString : MutableLiveData<String> = MutableLiveData()
    val tobeWrittenString : LiveData<String> = _tobeWrittenString

    private var previousString : String ?= null
    private val mapOfString = hashMapOf<String, Int>()

    private val _isLoading : MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

    fun closeModel()
    {
        _yolov5TFLiteDetector.value?.close()
    }

    private fun getOutputValue() {

            val tempResult = _yolov5TFLiteDetector.value?.getResult()
            Log.d("Update", tempResult.toString())
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
    }
    fun initModel(modelName : String, context : Context)
    {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                val model = ObjectDetectorStore(context).createModel(modelName).apply {
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