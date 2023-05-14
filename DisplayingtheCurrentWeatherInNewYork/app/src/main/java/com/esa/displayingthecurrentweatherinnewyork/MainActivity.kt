package com.esa.displayingthecurrentweatherinnewyork

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.esa.displayingthecurrentweatherinnewyork.api.OpenWeatherMapService
import com.esa.displayingthecurrentweatherinnewyork.model.OpenWeatherMapResponseData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MainActivity : AppCompatActivity() {
    private val titleView: TextView by lazy { findViewById(R.id.main_title) }
    private val statusView: TextView by lazy { findViewById(R.id.main_status) }
    private val descriptionView: TextView by lazy { findViewById(R.id.main_description) }
    private val weatherIconView: ImageView by lazy { findViewById(R.id.main_weather_icon) }
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val openWeatherMapService by lazy { retrofit.create(OpenWeatherMapService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayWeather()
    }

    private fun displayWeather() {
        val call = openWeatherMapService.getWeather("New York", "a2de2f269151f246f7bb5d9a299bc8bf")
        call.enqueue(object : Callback<OpenWeatherMapResponseData> {
            override fun onFailure(call: Call<OpenWeatherMapResponseData>, t: Throwable) {
                Log.e("MainActivity", "Failed to get results", t)
            }

            override fun onResponse(
                call: Call<OpenWeatherMapResponseData>,
                response: Response<OpenWeatherMapResponseData>
            ) {
                if (response.isSuccessful) {
                    val response = response.body()
                    titleView.text = response?.locationName
                    response?.weather?.firstOrNull()?.let { weather ->
                        statusView.text = weather.status
                        descriptionView.text = weather.description
                        Glide.with(this@MainActivity).load("https://openweathermap.org/img/wn/${weather.icon}@2x.png")
                            .centerInside().into(weatherIconView)
                    }
                }
            }
        })
    }
}
