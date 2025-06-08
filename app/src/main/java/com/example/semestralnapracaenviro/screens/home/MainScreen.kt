package com.example.semestralnapracaenviro.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.semestralnapracaenviro.R
import com.example.semestralnapracaenviro.navigation.ScreenRoute
import com.example.semestralnapracaenviro.screens.map.MainScreenViewModel

/**
 * Hlavná obrazovka aplikácie po prihlásení používateľa.
 * Zobrazuje uvítací text a tri tlačidlá na navigáciu:
 * - nahlásenie skládky
 * - zobrazenie mapy
 * - prechod do profilu používateľa
 *
 * UI pozostáva z centrálne umiestnenej karty s bielym pozadím na zelenom pozadí.
 *
 * @param navController Navigačný controller na prepínanie medzi obrazovkami.
 * @param mainScreenViewModel ViewModel poskytujúci dáta a logiku pre túto obrazovku.
 *
 * @author Jakub Gubány
 */
@Composable
fun MainScreen(
    navController: NavController,
    mainScreenViewModel: MainScreenViewModel = viewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF2E7D32)), // zelené pozadie
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(32.dp)) // biela karta s oblými rohmi
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF2E7D32),
                    fontSize = 36.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.welcome_back_title),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF2E7D32)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(ScreenRoute.REPORT.route)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(24.dp))
            ) {
                Text(stringResource(R.string.report_button), color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(ScreenRoute.MAP.route)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(24.dp))
            ) {
                Text(stringResource(R.string.map_button_text), color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(ScreenRoute.PROFILE.route)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(24.dp))
            ) {
                Text(stringResource(R.string.profile_button_text), color = Color.White)
            }
        }
    }
}