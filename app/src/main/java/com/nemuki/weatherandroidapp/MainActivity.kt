package com.nemuki.weatherandroidapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cityName = ""
        val units = "metric"
        val apiKey = BuildConfig.OWM_API_KEY
        fetchApi(cityName, apiKey, units)
    }

    private fun fetchApi(cityName: String, apiKey: String, units: String) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        thread {
            try {
                val service: WeatherService = retrofit.create(WeatherService::class.java)
                val weatherApiResponse = service.fetchWeather(
                    cityName,
                    apiKey,
                    units
                ).execute().body()
                    ?: throw IllegalStateException("bodyがnullだよ！")

                Handler(Looper.getMainLooper()).post {
                    Log.d("response-weather", weatherApiResponse.toString())
                }
            } catch (e: Exception) {
                Log.d("response-weather", "debug $e")
            }
        }
    }
}