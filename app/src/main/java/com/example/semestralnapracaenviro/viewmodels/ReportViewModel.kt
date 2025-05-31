package com.example.semestralnapracaenviro.viewmodels // Alebo váš správny balíček

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application // Potrebné pre AndroidViewModel
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel // Použijeme AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.semestralnapracaenviro.data.model.AccessibilityLevel
// Uistite sa, že názov dátovej triedy je správny
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

    companion object {
        private const val TAG =
            "ReportViewModel"
    }


    @SuppressLint("MissingPermission")
    fun fetchCurrentDeviceLocation() {
        val appContext = getApplication<Application>().applicationContext


        if (ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "fetchCurrentDeviceLocation volané bez potrebných povolení.")
            submissionStatus = "Pre získanie polohy sú potrebné povolenia." // Informujeme cez stav

            isFetchingLocation = false
            return
        }

        if (isFetchingLocation) {
            Log.d(TAG, "Už prebieha získavanie polohy.")
            return
        }

        isFetchingLocation = true
        currentDeviceLocation = null

        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_LOW_POWER)
            .build()

        fusedLocationClient.getCurrentLocation(
            locationRequest,
            null
        )
            .addOnSuccessListener { location ->
                if (location != null) {
                    currentDeviceLocation = LatLng(location.latitude, location.longitude)
                    Log.d(
                        TAG,
                        "Aktuálna poloha zariadenia získaná: ${currentDeviceLocation?.latitude}, ${currentDeviceLocation?.longitude}"
                    )

                } else {
                    Log.w(TAG, "Nepodarilo sa získať aktuálnu polohu (location is null).")
                    submissionStatus =
                        "Nepodarilo sa získať aktuálnu polohu." // Informujeme cez stav

                }
                isFetchingLocation = false
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Chyba pri získavaní aktuálnej polohy zariadenia", e)
                submissionStatus =
                    "Chyba pri získavaní polohy: ${e.message}" // Informujeme cez stav

                isFetchingLocation = false
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