package com.example.semestralnapracaenviro.ui.map

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.semestralnapracaenviro.data.model.ReportData
import com.example.semestralnapracaenviro.viewmodels.DumpSitesViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    dumpSitesViewModel: DumpSitesViewModel = viewModel()
) {
    val uiState by dumpSitesViewModel.uiState
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Predvolená poloha a priblíženie kamery (napr. mesto martin)
    val slovakiaCenter = LatLng(49.061661, 18.919024)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(slovakiaCenter, 12f)
    }

    var selectedDumpSite by remember { mutableStateOf<ReportData?>(null) }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mapa nahlásených skládok") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Späť")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
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

                ),
                onMapLoaded = {
                    Log.d("MapScreen", "Mapa bola úspešne načítaná.")

                },
                onPOIClick = { poi ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "POI Kliknuté: ${poi.name}",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            ) {

                uiState.dumpSites.forEach { site ->


                    site.location?.let { geoPoint ->
                        val position = LatLng(geoPoint.latitude, geoPoint.longitude)
                        Marker(
                            state = rememberMarkerState(position = position),
                            title = "Skládka",
                            snippet = site.description.take(50) + "...",

                            onClick = {
                                Log.d("MapScreen", "Marker kliknutý: ${site.description}")
                                selectedDumpSite = site
                                false
                            }
                        )
                    }
                }
            }


            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }


            uiState.errorMessage?.let { message ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp), contentAlignment = Alignment.Center) {
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


    selectedDumpSite?.let { site ->
        AlertDialog(
            onDismissRequest = { selectedDumpSite = null },
            title = { Text("Detail skládky") },
            text = {
                Column {
                    Text("Popis: ${site.description}")

                    Text("Prístupnosť: ${site.accessibility}")

                    site.timestamp?.let {

                        val date = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                            .format(java.util.Date(it))
                        Text("Nahlásené dňa: $date")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        dumpSitesViewModel.markSiteAsCleaned(site)
                    },

                ) {
                    if (uiState.isMarkingAsCleaned) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Upratané")
                    }
                }
                Button(onClick = { selectedDumpSite = null }) {
                    Text("Zavrieť")
                }
            },
            icon = { Icon(Icons.Filled.Info, contentDescription = null) }
        )
    }
}