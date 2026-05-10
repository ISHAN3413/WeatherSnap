package com.oceanx.weathersnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class CameraUiState {
    object Idle : CameraUiState()
    object Capturing : CameraUiState()
    data class Captured(val path: String) : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Idle)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun onCapturing() { _uiState.value = CameraUiState.Capturing }
    fun onCaptured(path: String) { _uiState.value = CameraUiState.Captured(path) }
    fun onError(msg: String) { _uiState.value = CameraUiState.Error(msg) }
}