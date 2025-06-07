package com.example.semestralnapracaenviro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.semestralnapracaenviro.screens.home.MainScreen
import com.example.semestralnapracaenviro.screens.login.LoginScreen
import com.example.semestralnapracaenviro.screens.map.MapScreen
import com.example.semestralnapracaenviro.screens.profile.ProfileScreen
import com.example.semestralnapracaenviro.screens.register.RegisterScreen
import com.example.semestralnapracaenviro.screens.welcome.WelcomeScreen
import com.example.semestralnapracaenviro.screens.report.ReportDumpScreen

/**
 * Hlavný navigačný hostiteľ aplikácie.
 *
 * Zabezpečuje definovanie všetkých hlavných obrazoviek a ciest medzi nimi.
 *
 * @param navController Navigačný kontrolér pre riadenie prechodu medzi obrazovkami.
 * @param modifier Modifier pre prispôsobenie vzhľadu a rozloženia navigačného hostiteľa.
 */
@Composable
fun AppNavHost(
    navController : NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.WELCOME.route,   // Štartovacia obrazovka aplikácie
        modifier = modifier
    ) {
        // Definícia jednotlivých ciest pre obrazovky

        composable(ScreenRoute.WELCOME.route) {
            WelcomeScreen(navController = navController)
        }

        composable(ScreenRoute.REGISTRATION.route) {
            RegisterScreen(navController = navController)
        }

        composable(ScreenRoute.LOGIN.route) {
            LoginScreen(navController = navController)
        }

        composable(ScreenRoute.MAINSCREEN.route) {
            MainScreen(navController = navController)
        }

        composable(ScreenRoute.REPORT.route) {
            ReportDumpScreen(navController = navController)
        }

        composable(ScreenRoute.MAP.route) {
            MapScreen(navController = navController)
        }

        composable(ScreenRoute.PROFILE.route) {
            ProfileScreen(navController = navController)
        }
    }
}