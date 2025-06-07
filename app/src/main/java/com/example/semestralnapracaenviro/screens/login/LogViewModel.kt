package com.example.semestralnapracaenviro.screens.login

import android.util.Log
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
     * Overí, či sú vstupné údaje správne zadané.
     * @return true, ak sú vstupy validné, inak false.
     */
    private fun validateInputs(): Boolean {
        emailError = null
        passwordError = null
        var isValid = true

        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email format"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    /**
     * Pokúsi sa prihlásiť používateľa na základe zadaného emailu a hesla.
     * Nastavuje stavové premenne podľa výsledku.
     */
    fun logInUser() {
        if (!validateInputs()) {
            isLoading = false
            return
        }

        isLoading = true
        loginStatus = null

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email.trim(), password).await()
                Log.d("LoginViewModel", "User signed in successfully.")
                loginStatus = "Login successful!"

            } catch (e: FirebaseAuthInvalidUserException) {
                Log.w("LoginViewModel", "Login failed: User not found.", e)
                emailError = "User with this email does not exist."
                loginStatus = "Login failed: User not found."
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                Log.w("LoginViewModel", "Login failed: Invalid credentials.", e)
                passwordError = "Incorrect password."
                loginStatus = "Login failed: Incorrect password."
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login failed with exception.", e)
                loginStatus = "Login failed: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Vymaže údaje formulára a resetuje chybové hlásenia.
     */
    fun clearForm() {
        email = ""
        password = ""
        emailError = null
        passwordError = null
        isLoading = false
        loginStatus = null
    }

    /**
     * Vymaže stav loginStatus. Použiteľné po spracovaní správy v UI.
     */
    fun clearLoginStatus() {
        loginStatus = null
    }
}