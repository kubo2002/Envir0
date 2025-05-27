package com.example.semestralnapracaenviro.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainScreenViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    fun logout() {
        auth.signOut()
        // Po odhlásení by mala nasledovať navigácia na prihlasovaciu obrazovku
    }

}