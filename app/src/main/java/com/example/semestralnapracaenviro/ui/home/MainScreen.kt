package com.example.semestralnapracaenviro.ui.home
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.semestralnapracaenviro.viewmodels.MainScreenViewModel
import com.example.semestralnapracaenviro.navigation.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    mainScreenViewModel: MainScreenViewModel = viewModel()
) {
    val userEmail = mainScreenViewModel.currentUserEmail

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hlavná obrazovka") },
                actions = {
                    IconButton(onClick = {
                        mainScreenViewModel.logout()
                        // Navigácia po odhlásení
                        navController.navigate(ScreenRoute.LOGIN.route) {
                            popUpTo(ScreenRoute.MAINSCREEN.route) { inclusive = true } // Odstráni MainScreen z backstacku
                            launchSingleTop = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Odhlásiť sa"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp), // Pridanie ďalšieho paddingu pre obsah
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (userEmail != null) {
                Text("Vitajte, $userEmail!", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                Text("Vitajte!", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(24.dp))
            }

            Button(
                onClick = {
                    // Navigácia na obrazovku pre nahlásenie skládky
                    navController.navigate(ScreenRoute.REPORT.route)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nahlásiť skládku")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(ScreenRoute.MAP.route) // Navigácia na MapScreen
                },
                modifier = Modifier.fillMaxWidth()

            ) {
                Text("Zobraziť mapu skládok")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // TODO: Navigovať na obrazovku "Naplánované čistenia"
                    // navController.navigate(ScreenRoute.SCHEDULED_CLEANUPS.route) // Príklad
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Zatiaľ deaktivované
            ) {
                Text("Naplánované čistenia (už čoskoro)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // TODO: Navigovať na obrazovku "Profil"
                    // navController.navigate(ScreenRoute.PROFILE.route) // Príklad
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = false // Zatiaľ deaktivované
            ) {
                Text("Profil (už čoskoro)")
            }
        }
    }
}