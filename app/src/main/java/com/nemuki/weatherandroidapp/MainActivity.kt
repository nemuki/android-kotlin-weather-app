package com.nemuki.weatherandroidapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editCityNameText = findViewById<EditText>(R.id.cityName)
        val postButton = findViewById<Button>(R.id.postButton)

        postButton.setOnClickListener {
            fetchApi(editCityNameText.text.toString(), "metric")
        }
    }

    private fun fetchApi(cityName: String, units: String) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        val tempText = findViewById<TextView>(R.id.tempText)
        val weatherIcon = findViewById<ImageView>(R.id.imageView)

        thread {
            try {
                val service: WeatherService = retrofit.create(WeatherService::class.java)
                val weatherApiResponse = service.fetchWeather(
                    cityName,
                    BuildConfig.OWM_API_KEY,
                    units
                ).execute().body()
                    ?: throw IllegalStateException("bodyがnullだよ！")

                Handler(Looper.getMainLooper()).post {
                    Log.d("response-weather", weatherApiResponse.weather.toString())
                    val weatherIconUrl = weatherApiResponse.weather[0].icon
                    tempText.text = weatherApiResponse.main.temp.toString()
                    Glide
                        .with(this)
                        .load("https://openweathermap.org/img/wn/$weatherIconUrl@2x.png")
                        .into(weatherIcon)
                }
            } catch (e: Exception) {
                Log.d("response-weather", "debug $e")
            }
        }
    }
}