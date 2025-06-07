package com.example.semestralnapracaenviro.screens.report

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapracaenviro.data.model.AccessibilityLevel
import com.example.semestralnapracaenviro.data.model.ReportData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel zodpovedný za správu stavu a logiku pre nahlasovanie skládok.
 * Umožňuje získavanie aktuálnej polohy zariadenia a odosielanie reportov do Firestore.
 *
 * @property description Textový popis skládky.
 * @property selectedAccessibility Vybraná úroveň prístupnosti skládky.
 * @property currentDeviceLocation Aktuálna poloha zariadenia ako LatLng, alebo null ak nie je dostupná.
 * @property isFetchingLocation Indikuje, či prebieha získavanie polohy.
 * @property submissionStatus Stav odoslania reportu, vrátane chýb a úspechov.
 * @property isSubmitting Indikuje, či prebieha odosielanie reportu.
 * @property locationError Chyba týkajúca sa získavania polohy, ak existuje.
 *
 * @author Jakub Gubány
 */
class ReportViewModel(application: Application) : AndroidViewModel(application) {

    var description by mutableStateOf("")
    var selectedAccessibility by mutableStateOf(AccessibilityLevel.EASY)

    var currentDeviceLocation by mutableStateOf<LatLng?>(null)
        private set
    var isFetchingLocation by mutableStateOf(false)
        private set

    var submissionStatus by mutableStateOf<String?>(null)
        private set
    var isSubmitting by mutableStateOf(false)
        private set

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    var locationError by mutableStateOf<String?>(null)
        private set

    companion object {
        private const val TAG =
            "ReportViewModel"
    }

    /**
     * Pokúsi sa získať aktuálnu polohu zariadenia.
     * Vyžaduje udelené povolenia ACCESS_FINE_LOCATION alebo ACCESS_COARSE_LOCATION.
     * Ak povolenia nie sú udelené, nastaví `locationError` a ukončí proces.
     * Výsledná poloha sa uloží do `currentDeviceLocation`.
     *
     * Používa FusedLocationProviderClient s vysokou presnosťou.
     *
     * @throws SecurityException ak povolenia nie sú udelené (interné použitie).
     *
     * @author Jakub Gubány
     */
    @SuppressLint("MissingPermission")
    fun fetchCurrentDeviceLocation() {
        val appContext = getApplication<Application>().applicationContext
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            locationError = "Location permissions are not granted."
            Log.w(TAG, "fetchCurrentDeviceLocationMinimal: Permissions denied.")
            return
        }

        isFetchingLocation = true
        locationError = null
        currentDeviceLocation = null

        Log.d(TAG, "fetchCurrentDeviceLocationMinimal: Starting to fetch location.")


        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null) // null pre CancellationToken
            .addOnSuccessListener { location: Location? ->
                isFetchingLocation = false
                if (location != null) {
                    currentDeviceLocation = LatLng(location.latitude, location.longitude) // Alebo priamo location
                    locationError = null
                    Log.i(TAG, "fetchCurrentDeviceLocationMinimal: Location successfully obtained. $currentDeviceLocation")
                } else {
                    currentDeviceLocation = null
                    locationError = "Failed to obtain a valid location (location is null)."
                    Log.w(TAG, "fetchCurrentDeviceLocationMinimal: location is null.")
                }
            }
            .addOnFailureListener { e ->
                isFetchingLocation = false
                currentDeviceLocation = null
                locationError = "Chyba pri získavaní polohy: ${e.message}" // Používame e.message pre stručnosť
                Log.e(TAG, "fetchCurrentDeviceLocationMinimal: Error while fetching location.", e)
            }
    }

    /**
     * Odosiela report o skládke do Firestore.
     * Overuje, či je zadaný popis skládky a či je dostupná aktuálna poloha.
     * V prípade chyby nastaví `submissionStatus` s popisom problému.
     * Po úspešnom odoslaní resetuje stav formulára.
     *
     * Používa Firebase Authentication pre získanie ID používateľa.
     *
     * @see ReportData model reportu, ktorý sa ukladá do Firestore.
     *
     * @author Jakub Gubány
     */
    fun submitReport() {
        val appContext = getApplication<Application>().applicationContext

        if (description.isBlank()) {
            submissionStatus = "The description of the landfill is mandatory."

            return
        }
        if (currentDeviceLocation == null) {
            submissionStatus =
                "Location is not available. Please obtain the current location before submitting."

            return
        }

        isSubmitting = true
        submissionStatus = null

        val reportData = ReportData(
            description = description.trim(),
            accessibility = selectedAccessibility,
            location = GeoPoint(
                currentDeviceLocation!!.latitude,
                currentDeviceLocation!!.longitude
            ),
            reportedBy = auth.currentUser?.uid,
            timestamp = System.currentTimeMillis()

        )

        viewModelScope.launch {
            try {

                db.collection("quick_dump_reports").add(reportData).await()
                submissionStatus = "Dump site has been reported!"
                Log.d(TAG, submissionStatus!!)

                description = ""
                selectedAccessibility = AccessibilityLevel.EASY
                currentDeviceLocation = null
            } catch (e: Exception) {
                submissionStatus = "Error : ${e.message}"
                Log.e(TAG, "Error submitting report", e)
            } finally {
                isSubmitting = false
            }
        }
    }

    /**
     * Vymaže aktuálny stav odoslania reportu.
     * Používateľ môže napríklad resetovať zobrazenie chýb alebo úspechov.
     *
     * @author Jakub Gubány
     */
    fun clearSubmissionStatus() {
        submissionStatus = null
    }

    /**
     * Zavolané pri ukončení ViewModelu.
     * Tu je možné uvoľniť zdroje alebo zaznamenať ukončenie.
     *
     * @author Jakub Gubány
     */
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ReportViewModel cleared.")
    }
}