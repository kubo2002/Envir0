package com.example.semestralnapracaenviro.viewmodels // Alebo váš správny balíček

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application // Potrebné pre AndroidViewModel
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel // Použijeme AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapracaenviro.data.model.AccessibilityLevel
import com.example.semestralnapracaenviro.data.model.ReportData
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    var locationError by mutableStateOf<String?>(null) // Špecifický error pre polohu
        private set

    companion object {
        private const val TAG =
            "ReportViewModel"
    }


    @SuppressLint("MissingPermission")
    fun fetchCurrentDeviceLocation() {
        val appContext = getApplication<Application>().applicationContext
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            locationError = "Povolenia pre polohu nie sú udelené."
            Log.w(TAG, "fetchCurrentDeviceLocationMinimal: Povolenia zamietnuté.")
            return
        }

        isFetchingLocation = true
        locationError = null
        currentDeviceLocation = null

        Log.d(TAG, "fetchCurrentDeviceLocationMinimal: Začínam získavať polohu.")


        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null) // null pre CancellationToken
            .addOnSuccessListener { location: Location? ->
                isFetchingLocation = false
                if (location != null) {
                    currentDeviceLocation = LatLng(location.latitude, location.longitude) // Alebo priamo location
                    locationError = null
                    Log.i(TAG, "fetchCurrentDeviceLocationMinimal: Poloha úspešne získaná: $currentDeviceLocation")
                } else {
                    currentDeviceLocation = null
                    locationError = "Nepodarilo sa získať platnú polohu (location is null)."
                    Log.w(TAG, "fetchCurrentDeviceLocationMinimal: Poloha je null.")
                }
            }
            .addOnFailureListener { e ->
                isFetchingLocation = false
                currentDeviceLocation = null
                locationError = "Chyba pri získavaní polohy: ${e.message}" // Používame e.message pre stručnosť
                Log.e(TAG, "fetchCurrentDeviceLocationMinimal: Chyba pri získavaní polohy.", e)
            }
    }


    fun submitReport() {
        val appContext = getApplication<Application>().applicationContext

        if (description.isBlank()) {
            submissionStatus = "Popis skládky je povinný."

            return
        }
        if (currentDeviceLocation == null) {
            submissionStatus =
                "Poloha nie je k dispozícii. Získajte aktuálnu polohu pred odoslaním."

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
                submissionStatus = "Skládka úspešne nahlásená!"
                Log.d(TAG, submissionStatus!!)

                description = ""
                selectedAccessibility = AccessibilityLevel.EASY
                currentDeviceLocation = null
            } catch (e: Exception) {
                submissionStatus = "Chyba pri odosielaní: ${e.message}"
                Log.e(TAG, "Error submitting report", e)
            } finally {
                isSubmitting = false
            }
        }
    }

    fun clearSubmissionStatus() {
        submissionStatus = null
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ReportViewModel cleared.")
    }
}