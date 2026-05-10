package com.oceanx.weathersnap.data.repository


import com.oceanx.weathersnap.data.local.WeatherReport
import com.oceanx.weathersnap.data.local.WeatherReportDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val dao: WeatherReportDao
) {
    fun getAllReports(): Flow<List<WeatherReport>> = dao.getAllReports()

    suspend fun saveReport(report: WeatherReport) = withContext(Dispatchers.IO) {
        dao.insert(report)
    }
}