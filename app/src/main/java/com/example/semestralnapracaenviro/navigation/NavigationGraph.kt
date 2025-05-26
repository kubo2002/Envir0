package com.example.semestralnapracaenviro.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavHost(
    navController : NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = ScreenRoute.WELCOME.route
) {
    NavHost(
        navController = navController,
        startDestination = ScreenRoute.WELCOME.route,
        modifier = modifier
    ) {


    }


}