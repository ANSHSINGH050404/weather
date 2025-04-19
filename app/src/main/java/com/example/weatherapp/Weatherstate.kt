package com.example.weatherapp

sealed class WeatherState {
    object Initial : WeatherState()
    object Loading : WeatherState()
    data class Success(val weather: WeatherData) : WeatherState()
    data class Error(val message: String) : WeatherState()
}

data class WeatherData(
    val cityName: String,
    val temperature: Int,
    val description: String,
    val humidity: Int,
    val windSpeed: Float
)

// 5. Weather API Service
package com.example.weatherapp

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Retrofit client
object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val weatherService: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
}

// API interface
interface WeatherService {
    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Response<WeatherResponse>
}

// Response models
data class WeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherInfo>,
    val wind: Wind
)

data class MainData(
    val temp: Float,
    val humidity: Int
)

data class WeatherInfo(
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Float
)