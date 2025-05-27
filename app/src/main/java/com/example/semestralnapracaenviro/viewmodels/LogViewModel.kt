package com.example.semestralnapracaenviro.viewmodels

import android.util.Log
import androidx.activity.result.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LogViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var emailError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)

    var loginStatus by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    /**
     * Validuje vstupné polia formulára.
     * @return true ak sú všetky povinné polia platné, inak false.
     */
    private fun validateInputs(): Boolean {

        // Reset predchádzajúcich chýb
        emailError = null
        passwordError = null
        var isValid = true

        if (email.isBlank()) {
            emailError = "Email je povinný"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Neplatný formát emailu"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "Heslo je povinné"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Heslo musí mať aspoň 6 znakov"
            isValid = false
        }

        return isValid
    }

    fun logInUser() {
        if (!validateInputs()) {
            isLoading = false // Ak validácia zlyhá, zastavíme načítavanie
            return
        }

        isLoading = true
        loginStatus = null // Reset statusu pred novým pokusom

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                Log.d("LoginViewModel", "User signed in successfully.")
                loginStatus = "Prihlásenie úspešné!"
                // Navigácia sa bude riešiť v Composable na základe tohto statusu

            } catch (e: FirebaseAuthInvalidUserException) {
                // Používateľ s týmto emailom neexistuje
                Log.w("LoginViewModel", "Login failed: User not found.", e)
                emailError = "Používateľ s týmto emailom neexistuje."
                loginStatus = "Prihlásenie zlyhalo: Používateľ neexistuje."
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                // Nesprávne heslo
                Log.w("LoginViewModel", "Login failed: Invalid credentials (wrong password).", e)
                passwordError = "Nesprávne heslo."
                loginStatus = "Prihlásenie zlyhalo: Nesprávne heslo."
            } catch (e: Exception) {
                // Ostatné chyby (napr. sieťové)
                Log.e("LoginViewModel", "Login failed with exception.", e)
                loginStatus = "Prihlásenie zlyhalo: ${e.localizedMessage ?: "Neznáma chyba"}"
            } finally {
                isLoading = false
            }

        }
    }

    fun clearForm() {
        email = ""
        password = ""
        emailError = null
        passwordError = null
        isLoading = false
        loginStatus = null
    }

    fun clearLoginStatus() {
        loginStatus = null
    }
}