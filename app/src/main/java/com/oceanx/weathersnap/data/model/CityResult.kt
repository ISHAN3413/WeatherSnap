package com.oceanx.weathersnap.data.model


import com.google.gson.annotations.SerializedName

data class CitySearchResponse(
    val results: List<CityResult>?
)

data class CityResult(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String?,
    @SerializedName("admin1") val state: String?
) {
    val displayName: String
        get() = buildString {
            append(name)
            state?.let { append(", $it") }
            country?.let { append(", $it") }
        }
}