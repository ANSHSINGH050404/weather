package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherApp()
                }
            }
        }
    }
}

@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // Determine start destination based on auth state
    val startDestination = if (auth.currentUser != null) {
        "weather"
    } else {
        "login"
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onNavigateToSignup = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("weather") {
                    popUpTo("login") { inclusive = true }
                }}
            )
        }
        composable("signup") {
            SignupScreen(
                onNavigateToLogin = { navController.navigate("login") {
                    popUpTo("signup") { inclusive = true }
                }},
                onSignupSuccess = { navController.navigate("weather") {
                    popUpTo("signup") { inclusive = true }
                }}
            )
        }
        composable("weather") {
            WeatherScreen(
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("weather") { inclusive = true }
                    }
                }
            )
        }
    }
}