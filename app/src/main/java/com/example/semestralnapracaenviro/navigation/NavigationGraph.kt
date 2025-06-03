package com.example.semestralnapracaenviro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.semestralnapracaenviro.ui.home.MainScreen
import com.example.semestralnapracaenviro.ui.login.LoginScreen
import com.example.semestralnapracaenviro.ui.map.MapScreen
import com.example.semestralnapracaenviro.ui.register.RegisterScreen
import com.example.semestralnapracaenviro.ui.welcome.WelcomeScreen
import com.example.semestralnapracaenviro.ui.report.ReportDumpScreen
@Composable
fun AppNavHost(
    navController : NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.WELCOME.route,   // startovacia obrazovka
        modifier = modifier
    ) {
        composable(ScreenRoute.WELCOME.route) {
            WelcomeScreen(navController = navController)
        }

        // sem pridam dalsie cesty k ostatnym obrazovkam

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


    }
}