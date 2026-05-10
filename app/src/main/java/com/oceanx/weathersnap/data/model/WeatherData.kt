package com.oceanx.weathersnap.data.model


data class WeatherData(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val condition: String,
    val humidity: Int,
    val windSpeed: Double,
    val pressure: Double,
    val latitude: Double,
    val longitude: Double
)