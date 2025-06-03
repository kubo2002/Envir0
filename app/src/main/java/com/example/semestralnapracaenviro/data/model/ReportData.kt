package com.example.semestralnapracaenviro.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class ReportData (
    @DocumentId
    var id: String = "",
    val description: String = "",
    val accessibility: AccessibilityLevel = AccessibilityLevel.EASY,
    val location: GeoPoint? = null,
    val reportedBy: String? = null,
    val timestamp: Long = System.currentTimeMillis()

) {
    // Prázdny konštruktor pre Firestore
    constructor() : this("", "", AccessibilityLevel.EASY, null, null, 0L)
}