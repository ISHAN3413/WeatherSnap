package com.oceanx.weathersnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.oceanx.weathersnap.data.local.WeatherReport
import com.oceanx.weathersnap.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SavedReportsViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {
    val reports: Flow<List<WeatherReport>> = repository.getAllReports()
}