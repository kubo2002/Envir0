package com.example.semestralnapracaenviro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.semestralnapracaenviro.navigation.AppNavHost
import com.example.semestralnapracaenviro.screens.theme.SemestralnaPracaEnviroTheme
import com.google.firebase.FirebaseApp

/**
 * Hlavná aktivita aplikácie.
 * Inicializuje Firebase a nastaví UI pomocou Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    /**
     * Metóda, ktorá sa spustí pri vytvorení aktivity.
     * Inicializuje Firebase, zapne edge-to-edge režim a nastaví hlavné UI s navigáciou.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializácia Firebase
        FirebaseApp.initializeApp(this)

        // Povolenie edge-to-edge zobrazenia
        enableEdgeToEdge()

        // Nastavenie obsahu UI
        setContent {
            SemestralnaPracaEnviroTheme {
                val navController = rememberNavController()

                // Scaffold zabezpečuje základné rozloženie obrazovky
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        modifier = Modifier
                            .padding(innerPadding) // Štartovacia obrazovka je definovaná v navigačnom grafe
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}
