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
import javax.inject.Inject

@HiltViewModel
class BugReportViewModel @Inject constructor(private val generalRepository: GeneralRepository): ViewModel() {

    val addNewReportBugResult: LiveData<ApiResponse<BugReportResponse>> by lazy { _addNewReportBugResult }
    private val _addNewReportBugResult = MutableLiveData<ApiResponse<BugReportResponse>>()

    fun addNewReportBug(reportBugBody: ReportBugBody, file: FileRequest) {
        viewModelScope.launch {
            generalRepository.addNewReportBug(reportBugBody, file).collect {
                _addNewReportBugResult.postValue(it)
            }
        }
    }

}