package com.example.semestralnapracaenviro.ui.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.semestralnapracaenviro.navigation.ScreenRoute
import com.example.semestralnapracaenviro.ui.theme.SemestralnaPracaEnviroTheme

@Composable
fun WelcomeScreen(
    navController: NavController, // potrebujem nav kontroleruu dat vediet ze welcome screen bude jeho sucastou
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        // tu definujem komponenty ktore sa budu zobrazovat na obrazovke
        Button(
            onClick = {
                navController.navigate(ScreenRoute.REGISTRATION.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create account")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate(ScreenRoute.LOGIN.route)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log in")
        }

    }

}

@Preview(showBackground = true, name = "ukazka uvodnej obrazovky")
@Composable
fun WelcomeScreenPreview() {
    SemestralnaPracaEnviroTheme {
        val navController = rememberNavController()
        WelcomeScreen(navController = navController)
    }
}