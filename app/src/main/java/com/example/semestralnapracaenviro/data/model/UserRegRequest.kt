package com.example.semestralnapracaenviro.data.model

data class UserRegRequest(
    val uid: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val age: Int?,
    val password: String
)