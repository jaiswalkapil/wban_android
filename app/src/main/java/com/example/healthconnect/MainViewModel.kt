package com.example.healthconnect

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val healthConnectRepository = HealthConnectRepository(application)

    private val _insertState = MutableStateFlow<Result<Unit>?>(null)
    val insertState: StateFlow<Result<Unit>?> = _insertState

    fun insertDummyData() {
        viewModelScope.launch {
            val result = healthConnectRepository.insertDummyData()
            _insertState.value = result
        }
    }
}