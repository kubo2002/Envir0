package com.example.semestralnapracaenviro.screens.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.semestralnapracaenviro.R
import com.example.semestralnapracaenviro.navigation.ScreenRoute
import com.example.semestralnapracaenviro.screens.theme.SemestralnaPracaEnviroTheme

/**
 * Úvodná obrazovka aplikácie.
 *
 * Obsahuje názov aplikácie, slogan a dve tlačidlá pre navigáciu na prihlasovaciu
 * a registračnú obrazovku.
 *
 * Zobrazuje bielu kartu na zelenom pozadí s centrálnym zarovnaním.
 *
 * @param navController NavController pre navigáciu medzi obrazovkami.
 * @param modifier Modifier pre prispôsobenie vzhľadu komponentu.
 */
@Composable
fun WelcomeScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF2E7D32)), // Zelené pozadie
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Biela karta
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 40.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.app_title),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.app_slogan),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate(ScreenRoute.LOGIN.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text(stringResource(R.string.login_button), color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { navController.navigate(ScreenRoute.REGISTRATION.route) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32))
                ) {
                    Text(stringResource(R.string.signup_button))
                }
            }
        }
    }
}

/**
 * Náhľad úvodnej obrazovky pre vývoj a dizajn.
 *
 * Používa tému aplikácie a predvolený NavController.
 */
@Preview(showBackground = true, name = "Preview WelcomeScreen")
@Composable
fun WelcomeScreenPreview() {
    SemestralnaPracaEnviroTheme {
        val navController = rememberNavController()
        WelcomeScreen(navController = navController)
    }
}