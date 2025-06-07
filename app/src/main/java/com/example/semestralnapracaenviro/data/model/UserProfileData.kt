package com.example.semestralnapracaenviro.data.model

data class UserProfile(
    val uid: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val score: Long = 0
)