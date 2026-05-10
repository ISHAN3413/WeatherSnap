package com.oceanx.weathersnap.data.repository


import com.oceanx.weathersnap.data.api.GeocodingApi
import com.oceanx.weathersnap.data.api.WeatherApi
import com.oceanx.weathersnap.data.model.CityResult
import com.oceanx.weathersnap.data.model.WeatherData
import com.oceanx.weathersnap.util.WeatherCodeMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi
) {
    private val suggestionCache = HashMap<String, List<CityResult>>()

    suspend fun searchCities(query: String): Result<List<CityResult>> {
        suggestionCache[query]?.let { return Result.success(it) }
        return try {
            val results = geocodingApi.searchCities(query).results ?: emptyList()
            suggestionCache[query] = results
            Result.success(results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWeather(city: CityResult): Result<WeatherData> {
        return try {
            val response = weatherApi.getWeather(city.latitude, city.longitude)
            val current = response.current!!
            Result.success(
                WeatherData(
                    cityName = city.name,
                    country = city.country ?: "",
                    temperature = current.temperature,
                    condition = WeatherCodeMapper.map(current.weatherCode),
                    humidity = current.humidity,
                    windSpeed = current.windSpeed,
                    pressure = current.pressure,
                    latitude = city.latitude,
                    longitude = city.longitude
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}