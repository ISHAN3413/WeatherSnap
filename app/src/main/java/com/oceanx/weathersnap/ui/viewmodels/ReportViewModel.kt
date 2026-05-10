package com.oceanx.weathersnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oceanx.weathersnap.data.local.WeatherReport
import com.oceanx.weathersnap.data.model.WeatherData
import com.oceanx.weathersnap.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReportUiState {
    object Idle : ReportUiState()
    object Saving : ReportUiState()
    object Saved : ReportUiState()
    data class Error(val message: String) : ReportUiState()
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    private val _imagePath = MutableStateFlow<String?>(null)
    val imagePath: StateFlow<String?> = _imagePath.asStateFlow()

    private val _originalKb = MutableStateFlow(0L)
    val originalKb: StateFlow<Long> = _originalKb.asStateFlow()

    private val _compressedKb = MutableStateFlow(0L)
    val compressedKb: StateFlow<Long> = _compressedKb.asStateFlow()

    fun setImage(path: String, originalKb: Long, compressedKb: Long) {
        _imagePath.value = path
        _originalKb.value = originalKb
        _compressedKb.value = compressedKb
    }

    fun saveReport(weather: WeatherData, notes: String) {
        val path = _imagePath.value ?: run {
            _uiState.value = ReportUiState.Error("Please capture a photo first")
            return
        }
        viewModelScope.launch {
            _uiState.value = ReportUiState.Saving
            try {
                repository.saveReport(
                    WeatherReport(
                        cityName = weather.cityName,
                        country = weather.country,
                        temperature = weather.temperature,
                        condition = weather.condition,
                        humidity = weather.humidity,
                        windSpeed = weather.windSpeed,
                        pressure = weather.pressure,
                        imagePath = path,
                        originalSizeKb = _originalKb.value,
                        compressedSizeKb = _compressedKb.value,
                        notes = notes
                    )
                )
                _uiState.value = ReportUiState.Saved
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(e.message ?: "Failed to save")
            }
        }
    }

    fun resetState() {
        _uiState.value = ReportUiState.Idle
    }
}