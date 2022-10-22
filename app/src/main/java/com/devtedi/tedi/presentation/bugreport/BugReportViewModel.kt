package com.devtedi.tedi.presentation.bugreport

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.data.remote.general.BugReportResponse
import com.devtedi.tedi.data.remote.general.report.ReportBugBody
import com.devtedi.tedi.data.remote.general.request.FileRequest
import com.devtedi.tedi.data.repository.GeneralRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class BugReportViewModel @Inject constructor(private val generalRepository: GeneralRepository) :
    ViewModel() {

    val addNewReportBugResult: LiveData<ApiResponse<BugReportResponse>> by lazy { _addNewReportBugResult }
    private val _addNewReportBugResult = MutableLiveData<ApiResponse<BugReportResponse>>()

    fun addNewReportBug(
        name: RequestBody,
        email: RequestBody,
        message: RequestBody,
        severity: RequestBody,
        phoneBrand: RequestBody,
        ram: RequestBody,
        androidVersion: RequestBody,
        file: MultipartBody.Part
    ) {
        viewModelScope.launch {
            generalRepository.addNewReportBug(
                name,
                email,
                message,
                severity,
                phoneBrand,
                ram,
                androidVersion,
                file
            ).collect {
                _addNewReportBugResult.postValue(it)
            }
        }
    }

}