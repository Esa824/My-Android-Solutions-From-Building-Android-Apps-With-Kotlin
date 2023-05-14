package com.esa.displayingthecurrentweatherinnewyork.model

import com.squareup.moshi.Json

data class OpenWeatherMapResponseData(
    @field:Json(name = "name")
    val locationName: String,
    val weather: List<OpenWeatherMapWeatherData>
)
