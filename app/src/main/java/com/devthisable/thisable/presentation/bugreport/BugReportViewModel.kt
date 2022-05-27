package com.devthisable.thisable.presentation.bugreport

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devthisable.thisable.data.remote.ApiResponse
import com.devthisable.thisable.data.remote.general.BugReportResponse
import com.devthisable.thisable.data.repository.GeneralRepository
import com.devthisable.thisable.data.remote.general.report.ReportBugBody
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BugReportViewModel @Inject constructor(private val generalRepository: GeneralRepository): ViewModel() {

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