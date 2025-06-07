package com.example.semestralnapracaenviro.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.semestralnapracaenviro.data.model.UserProfile

class ProfileViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut.asStateFlow()

    init {
        fetchUserProfileFromAuth()
    }

    /**
     * Načíta profil aktuálne prihláseného používateľa z Firebase Auth.
     */
    fun fetchUserProfileFromAuth() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val currentUser = auth.currentUser

            if (currentUser == null) {
                _error.value = "Používateľ nie je prihlásený."
                _userProfile.value = null
                _isLoading.value = false
                _isLoggedOut.value = true
                return@launch
            }

            try {
                _userProfile.value = UserProfile(
                    uid = currentUser.uid,
                    email = currentUser.email,
                    firstName = currentUser.displayName?.split(" ")?.getOrNull(0),
                    lastName = currentUser.displayName?.split(" ")?.getOrNull(1),
                )
            } catch (e: Exception) {
                _error.value = "Chyba pri spracovaní profilu: ${e.message}"
                _userProfile.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Odhlási používateľa z Firebase a resetuje profilové údaje.
     */
    fun logoutUser() {
        viewModelScope.launch {
            try {
                auth.signOut()
                _isLoggedOut.value = true
                _userProfile.value = null
            } catch (e: Exception) {
                _error.value = "Chyba pri odhlasovaní: ${e.message}"
            }
        }
    }

    /**
     * Resetuje stav odhlásenia (napr. po navigácii).
     */
    fun resetLogoutState() {
        _isLoggedOut.value = false
    }
}