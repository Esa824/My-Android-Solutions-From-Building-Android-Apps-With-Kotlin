package com.esa.displayingthecurrentweatherinnewyork.api

import com.esa.displayingthecurrentweatherinnewyork.model.OpenWeatherMapResponseData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherMapService {
    @GET("weather")
    fun getWeather(
        @Query("q") location: String,
        @Query("appid") token: String
        ) : Call<OpenWeatherMapResponseData>
}