package com.devthisable.thisable.presentation.feature_text

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionRequest
import com.devthisable.thisable.data.remote.visionapi.model.TextDetectionResponse
import com.devthisable.thisable.data.repository.VisionApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextDetectionViewModel @Inject constructor(private val repository: VisionApiRepository): ViewModel() {

    fun textDetection(apiKey: String, textDetectionRequest: TextDetectionRequest) : LiveData<ApiResponse<TextDetectionResponse>> {
        val response = MutableLiveData<ApiResponse<TextDetectionResponse>>()
        viewModelScope.launch {
            repository.textDetection(apiKey, textDetectionRequest).collect {
                response.postValue(it)
            }
        }
        return response
    }

}