package com.example.semestralnapracaenviro.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.semestralnapracaenviro.data.model.ReportData
import com.example.semestralnapracaenviro.screens.report.DumpSitesViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

/**
 * Kompozičná funkcia zobrazujúca obrazovku s mapou.
 *
 * Na mape sa zobrazujú markery so skládky podľa údajov z [DumpSitesViewModel].
 * Používateľ môže kliknúť na marker, čím sa otvorí dialóg s detailmi skládky,
 * kde je možné skládku označiť za vyčistenú.
 *
 * @param navController Navigačný kontrolér pre ovládanie spätného návratu.
 * @param dumpSitesViewModel ViewModel poskytujúci dáta o skládke a ich stav.
 *
 * @author Jakub Gubány
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    dumpSitesViewModel: DumpSitesViewModel = viewModel()
) {
    val uiState by dumpSitesViewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }

    // Prednastavená pozícia kamery na centrum Slovenska (Martin)
    val slovakiaCenter = LatLng(49.061661, 18.919024)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(slovakiaCenter, 12f)
    }

    // Vybraná skládka pre zobrazenie detailov v dialógu
    var selectedDumpSite by remember { mutableStateOf<ReportData?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Dump Sites") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFF2E7D32)), // Zelené pozadie
        ) {
            // Zobrazenie Google Mapy s markerami skládok
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = true,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = true
                ),
                properties = MapProperties(
                    isMyLocationEnabled = false
                )
            ) {

                uiState.dumpSites.forEach { site ->

                    site.location?.let { geoPoint ->
                        val position = LatLng(geoPoint.latitude, geoPoint.longitude)
                        Marker(
                            state = rememberMarkerState(position = position),
                            title = "Site",
                            snippet = site.description.take(50) + "...",

                            onClick = {
                                selectedDumpSite = site
                                false
                            }
                        )
                    }
                }
            }

            // Zobrazovanie indikátora načítavania pri načítaní dát
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Zobrazovanie chybovej správy pri chybe
            uiState.errorMessage?.let { message ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Dialóg s detailmi vybratej skládky
    selectedDumpSite?.let { site ->
        AlertDialog(
            onDismissRequest = { selectedDumpSite = null },
            title = { Text("Details") },
            text = {
                Column {
                    Text("Description: ${site.description}")
                    Text("Accessibility: ${site.accessibility}")
                    site.timestamp?.let {
                        val date = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(it))
                        Text("Reported: $date")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        dumpSitesViewModel.markSiteAsCleaned(site)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                    if (uiState.isMarkingAsCleaned) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Mark as Cleaned")
                    }
                }
                Button(
                    onClick = { selectedDumpSite = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Close")
                }
            },
            icon = { Icon(Icons.Filled.Info, contentDescription = null) }
        )
    }
}
