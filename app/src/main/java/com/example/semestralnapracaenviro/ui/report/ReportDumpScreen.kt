package com.example.semestralnapracaenviro.ui.report
import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.semestralnapracaenviro.data.model.AccessibilityLevel
import com.example.semestralnapracaenviro.navigation.ScreenRoute
import com.example.semestralnapracaenviro.viewmodels.ReportViewModel // Použijeme existujúci ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(submissionStatusValue) {
        submissionStatusValue?.let { status ->
            Toast.makeText(context, status, Toast.LENGTH_LONG).show()
            reportViewModel.clearSubmissionStatus()
            if (status.startsWith("Skládka úspešne nahlásená!")) {
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
                Toast.makeText(context, "Povolenia zamietnuté. Hlásenie nemôže byť odoslané.", Toast.LENGTH_LONG).show()
                submissionAttempted = false
            }
        }
    )

    LaunchedEffect(currentDeviceLocation, submissionAttempted, isFetchingLocation) {
        if (submissionAttempted && !isFetchingLocation && currentDeviceLocation != null) {
            reportViewModel.submitReport()

        } else if (submissionAttempted && !isFetchingLocation && currentDeviceLocation == null) {

            if (reportViewModel.submissionStatus == null) {
                Toast.makeText(context, "Nepodarilo sa získať polohu. Skúste znova.", Toast.LENGTH_LONG).show()
            }
            submissionAttempted = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nahlásiť skládku", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Popis skládky") },
            modifier = Modifier.fillMaxWidth(),
        )

        Text(
            text = "Obtiažnosť prístupu",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
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
                        AccessibilityLevel.EASY -> "Ľahký"
                        AccessibilityLevel.MEDIUM -> "Stredný"
                        AccessibilityLevel.HARD -> "Ťažký"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        if (reportViewModel.isSubmitting || (submissionAttempted && isFetchingLocation)) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (reportViewModel.isSubmitting || (submissionAttempted && isFetchingLocation)) {

                    return@Button
                }

                reportViewModel.description = description
                reportViewModel.selectedAccessibility = selectedAccessibility
                submissionAttempted = true

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    reportViewModel.fetchCurrentDeviceLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                }
            },
            enabled = !reportViewModel.isSubmitting && !(submissionAttempted && isFetchingLocation)
        ) {
            Text("Nahlásiť skládku")
        }
    }
}