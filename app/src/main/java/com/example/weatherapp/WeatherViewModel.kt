package com.example.weatherapp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class WeatherViewModel : ViewModel() {
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Initial)
    val weatherState: StateFlow<WeatherState> = _weatherState

    suspend fun getWeather(city: String) {
        if (city.isBlank()) {
            _weatherState.value = WeatherState.Error("Please enter a city name")
            return
        }

        _weatherState.value = WeatherState.Loading

        try {
            val weatherService = RetrofitClient.weatherService
            val response = weatherService.getWeatherByCity(city, API_KEY)

            if (response.isSuccessful) {
                response.body()?.let { weatherResponse ->
                    val weatherData = WeatherData(
                        cityName = weatherResponse.name,
                        temperature = weatherResponse.main.temp.toInt(),
                        description = weatherResponse.weather.firstOrNull()?.description ?: "Unknown",
                        humidity = weatherResponse.main.humidity,
                        windSpeed = weatherResponse.wind.speed
                    )
                    _weatherState.value = WeatherState.Success(weatherData)
                } ?: run {
                    _weatherState.value = WeatherState.Error("No weather data found")
                }
            } else {
                _weatherState.value = WeatherState.Error("Error: ${response.code()}")
            }
        } catch (e: IOException) {
            _weatherState.value = WeatherState.Error("Network error. Please check your connection")
        } catch (e: HttpException) {
            _weatherState.value = WeatherState.Error("HTTP error: ${e.message()}")
        } catch (e: Exception) {
            _weatherState.value = WeatherState.Error("An error occurred: ${e.message}")
        }
    }

    companion object {
        // OpenWeatherMap API Key - Replace with your own
        private const val API_KEY = "YOUR_API_KEY_HERE"
    }
}
