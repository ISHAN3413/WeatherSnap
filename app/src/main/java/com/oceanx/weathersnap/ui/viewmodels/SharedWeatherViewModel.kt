package com.oceanx.weathersnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.oceanx.weathersnap.data.model.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SharedWeatherViewModel @Inject constructor() : ViewModel() {
    private val _selectedWeather = MutableStateFlow<WeatherData?>(null)
    val selectedWeather: StateFlow<WeatherData?> = _selectedWeather.asStateFlow()

    fun setWeather(weather: WeatherData) {
        _selectedWeather.value = weather
    }
}