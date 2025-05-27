package com.example.semestralnapracaenviro.data.model

import android.icu.text.Transliterator.Position
import com.google.type.LatLng

data class DumpSite (
    val id: String,
    val position: LatLng,
    val title: String,
    val description: String? = null,
    val reportedBy: String? = null,

    val timestamp: Long? = null,

    val photoUrl: String? = null,
    val status: String? = null

    )
