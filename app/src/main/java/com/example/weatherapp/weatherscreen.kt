package com.example.weatherapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun WeatherScreen(
    onLogout: () -> Unit,
    viewModel: WeatherViewModel = viewModel()
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val searchText = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top bar with search and logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            user?.email?.let {
                Text(
                    text = "Welcome, ${it.substringBefore('@')}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            IconButton(onClick = onLogout) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
            }
        }

        // Search field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = searchText.value,
                onValueChange = { searchText.value = it },
                placeholder = { Text("Enter city name") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            Button(
                onClick = {
                    scope.launch {
                        viewModel.getWeather(searchText.value)
                    }
                }
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }

        // Weather content
        when (val state = weatherState) {
            is WeatherState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is WeatherState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }
            is WeatherState.Success -> {
                WeatherContent(state.weather)
            }
            WeatherState.Initial -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Search for a city to get weather information",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherContent(weather: WeatherData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weather.cityName,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "${weather.temperature}°C",
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = weather.description,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            WeatherDetailItem("Humidity", "${weather.humidity}%")
            WeatherDetailItem("Wind", "${weather.windSpeed} km/h")
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}