package com.example.semestralnapracaenviro.screens.report

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.semestralnapracaenviro.data.model.AccessibilityLevel
import com.example.semestralnapracaenviro.navigation.ScreenRoute

/**
 * Composable obrazovka na nahlásenie nelegálnych skládok.
 *
 * @param navController Navigačný kontrolér pre presuny medzi obrazovkami.
 * @param reportViewModel ViewModel, ktorý spravuje stav a logiku nahlásenia.
 */
@Composable
fun ReportDumpScreen(
    navController: NavController,
    reportViewModel: ReportViewModel = viewModel()
) {
    val context = LocalContext.current

    var description by remember { mutableStateOf("") }
    var selectedAccessibility by remember { mutableStateOf(AccessibilityLevel.EASY) }
    val submissionStatusValue = reportViewModel.submissionStatus
    val currentDeviceLocation = reportViewModel.currentDeviceLocation
    val isFetchingLocation = reportViewModel.isFetchingLocation
    var submissionAttempted by remember { mutableStateOf(false) }

    // Reakcia na zmenu stavu odoslania
    LaunchedEffect(submissionStatusValue) {
        submissionStatusValue?.let { status ->
            Toast.makeText(context, status, Toast.LENGTH_LONG).show()
            reportViewModel.clearSubmissionStatus()
            if (status.startsWith("Dump site has been reported!")) {
                // Po úspešnom nahlásení vyčisti vstupy a naviguj na hlavnú obrazovku
                description = ""
                selectedAccessibility = AccessibilityLevel.EASY
                submissionAttempted = false
                navController.navigate(ScreenRoute.MAINSCREEN.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
            } else {
                submissionAttempted = false
            }
        }
    }

    // Launcher na požiadavku povolení pre polohu zariadenia
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
            if (granted) {
                if (submissionAttempted) {
                    reportViewModel.fetchCurrentDeviceLocation()
                }
            } else {
                submissionAttempted = false
            }
        }
    )

    // Spustenie odoslania nahlásenia, keď je dostupná poloha a odosielanie bolo iniciované
    LaunchedEffect(currentDeviceLocation, submissionAttempted, isFetchingLocation) {
        if (submissionAttempted && !isFetchingLocation && currentDeviceLocation != null) {
            reportViewModel.submitReport()
        } else if (submissionAttempted && !isFetchingLocation && currentDeviceLocation == null) {
            if (reportViewModel.submissionStatus == null) {
                Toast.makeText(context, "Failed to obtain location. Please try again.", Toast.LENGTH_LONG).show()
            }
            submissionAttempted = false
        }
    }

    // Užívateľské rozhranie obrazovky
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF388E3C)), // Zelené pozadie
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Report Dump",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFF388E3C),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Accessibility level",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                AccessibilityLevel.values().forEach { level ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (level == selectedAccessibility),
                                onClick = { selectedAccessibility = level },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (level == selectedAccessibility),
                            onClick = null
                        )
                        Text(
                            text = when (level) {
                                AccessibilityLevel.EASY -> "Easily accessible"
                                AccessibilityLevel.MEDIUM -> "Moderately accessible"
                                AccessibilityLevel.HARD -> "Difficult to access"
                            },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Zobrazenie indikátora načítania počas odosielania alebo získavania polohy
                if (reportViewModel.isSubmitting || (submissionAttempted && isFetchingLocation)) {
                    CircularProgressIndicator()
                }

                Button(
                    onClick = {
                        if (reportViewModel.isSubmitting || (submissionAttempted && isFetchingLocation)) return@Button

                        // Nastavenie hodnôt vo ViewModel a spustenie procesu nahlásenia
                        reportViewModel.description = description
                        reportViewModel.selectedAccessibility = selectedAccessibility
                        submissionAttempted = true

                        // Overenie povolení pre prístup k polohe
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        ) {
                            reportViewModel.fetchCurrentDeviceLocation()
                        } else {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    },
                    enabled = !reportViewModel.isSubmitting && !(submissionAttempted && isFetchingLocation),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF388E3C),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Report Dump")
                }
            }
        }
    }
}