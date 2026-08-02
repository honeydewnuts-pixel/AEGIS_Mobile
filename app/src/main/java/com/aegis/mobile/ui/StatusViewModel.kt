package com.aegis.mobile.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aegis.mobile.models.AnalysisResponse

class StatusViewModel : ViewModel() {

    private val _currentSignal = MutableLiveData<AnalysisResponse?>()
    val currentSignal: LiveData<AnalysisResponse?> = _currentSignal

    private val _statusText = MutableLiveData("WAITING...")
    val statusText: LiveData<String> = _statusText

    fun updateSignal(response: AnalysisResponse?) {
        _currentSignal.value = response
        _statusText.value = response?.signal ?: "HOLD"
    }
}
