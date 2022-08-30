package com.devtedi.tedi.presentation.bugreport

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.data.remote.general.BugReportResponse
import com.devtedi.tedi.data.remote.general.report.ReportBugBody
import com.devtedi.tedi.data.repository.GeneralRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BugReportViewModel @Inject constructor(private val generalRepository: GeneralRepository): ViewModel() {

    val addNewReportBugResult: LiveData<Result<String>> by lazy { _addNewReportBugResult }
    private val _addNewReportBugResult = MutableLiveData<Result<String>>()

    fun addNewReportBug(reportBugBody: ReportBugBody): LiveData<ApiResponse<BugReportResponse>> {
        val response = MutableLiveData<ApiResponse<BugReportResponse>>()
        viewModelScope.launch {
            generalRepository.addNewReportBug(reportBugBody).collect {
                response.postValue(it)
            }
        }
        return response
    }

}