package com.example.semestralnapracaenviro.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint // Alternatíva pre Firestore, ak nepoužívate LatLng priamo
import com.google.firebase.firestore.ServerTimestamp // Pre serverovú časovú značku
import java.util.Date // Pre ServerTimestamp

data class DumpSite(
    val id: String = "",
    val positionLatLng: LatLng? = null,
    val positionGeoPoint: GeoPoint? = null,

    val title: String = "",
    val description: String? = null,
    val reportedBy: String? = null, // UID používateľa
    val timestamp: Long? = null,

    @ServerTimestamp
    val createdAt: Date? = null, // Firestore automaticky vyplní pri vytváraní dokumentu
    val photoUrl: String? = null,
    val status: String = "reported" // napr. "reported", "cleaned_scheduled", "cleaned"
) {
    constructor() : this(
        id = "",
        positionLatLng = null,
        positionGeoPoint = null,
        title = "",
        description = null,
        reportedBy = null,
        timestamp = null,
        createdAt = null,
        photoUrl = null,
        status = "reported"
    )
}