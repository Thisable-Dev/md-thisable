package com.devtedi.tedi.presentation.feature_currency

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtedi.tedi.factory.ObjectDetectorStore
import com.devtedi.tedi.factory.YOLOv5ModelCreator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CurrencyDetectionViewModel : ViewModel(){


    private val _yolov5TFLiteDetector : MutableLiveData<YOLOv5ModelCreator> = MutableLiveData()
    val yolov5TFLiteDetector : LiveData<YOLOv5ModelCreator> = _yolov5TFLiteDetector

    private val _isLoading : MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading : LiveData<Boolean> = _isLoading

    fun closeModel()
    {
        _yolov5TFLiteDetector.value?.close()
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
}