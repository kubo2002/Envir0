package com.example.semestralnapracaenviro.screens.map

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class MainScreenViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Získava email aktuálne prihláseného používateľa.
     */
    val currentUserEmail: String?
        get() = auth.currentUser?.email

    /**
     * Odhlási aktuálneho používateľa.
     * Po odhlásení by mala byť v UI spustená navigácia na login obrazovku.
     */
    fun logout() {
        auth.signOut()
    }
}