package com.oceanx.weathersnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oceanx.weathersnap.data.model.CityResult
import com.oceanx.weathersnap.data.model.WeatherData
import com.oceanx.weathersnap.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()
    data class Success(val weather: WeatherData, val city: CityResult) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _suggestions = MutableStateFlow<List<CityResult>>(emptyList())
    val suggestions: StateFlow<List<CityResult>> = _suggestions.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        _suggestions.value = emptyList()
        if (newQuery.length <= 2) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            repository.searchCities(newQuery)
                .onSuccess { _suggestions.value = it }
        }
    }

    fun fetchWeather(city: CityResult) {
        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading
            _suggestions.value = emptyList()
            _query.value = city.displayName
            repository.getWeather(city)
                .onSuccess { _uiState.value = WeatherUiState.Success(it, city) }
                .onFailure { _uiState.value = WeatherUiState.Error(it.message ?: "Something went wrong") }
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }
}