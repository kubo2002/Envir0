package com.example.semestralnapracaenviro.screens.register

// Android
import android.util.Log

// Jetpack Compose
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

// AndroidX Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

// Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Kotlin
import kotlin.text.isBlank
import kotlin.text.isNotBlank
import kotlin.text.toIntOrNull
import kotlin.text.trim

// Coroutines
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Local (data model)
import com.example.semestralnapracaenviro.data.model.UserRegRequest

/**
 * ViewModel pre obrazovku registrácie používateľa.
 * Obsahuje stavové premenné pre vstupné polia a validáciu,
 * logiku registrácie používateľa cez Firebase Authentication a Firestore.
 */
class RegViewModel : ViewModel() {
    var email by mutableStateOf("")
    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var ageString by mutableStateOf("")

    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var emailError by mutableStateOf<String?>(null)
    var firstNameError by mutableStateOf<String?>(null)
    var lastNameError by mutableStateOf<String?>(null)
    var ageError by mutableStateOf<String?>(null)
    var passwordError by mutableStateOf<String?>(null)
    var confirmPasswordError by mutableStateOf<String?>(null)

    var registrationStatus by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val dbUsers = FirebaseFirestore.getInstance()

    /**
     * Overí vstupné údaje formulára.
     * Nastaví chybové správy a vráti true len ak sú všetky údaje platné.
     */
    private fun validateInputs(): Boolean {
        emailError = null
        firstNameError = null
        lastNameError = null
        ageError = null
        passwordError = null
        confirmPasswordError = null
        var isValid = true

        if (firstName.isBlank()) {
            firstNameError = "First name is required"
            isValid = false
        }

        if (lastName.isBlank()) {
            lastNameError = "Last name is required"
            isValid = false
        }

        if (email.isBlank()) {
            emailError = "Email is required"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Invalid email format"
            isValid = false
        }

        if (ageString.isNotBlank()) {
            val ageValue = ageString.toIntOrNull()
            if (ageValue == null || ageValue <= 0 || ageValue > 120) {
                ageError = "Invalid age"
                isValid = false
            }
        }

        if (password.isBlank()) {
            passwordError = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Password confirmation is required"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "Passwords do not match"
            isValid = false
        }

        return isValid
    }

    /**
     * Spustí proces registrácie používateľa.
     * Vytvorí účet vo Firebase a uloží údaje do Firestore.
     * Volá spätnú väzbu po úspešnej registrácii.
     */
    fun registerUser(onRegistrationSuccess: () -> Unit) {
        if (!validateInputs()) {
            registrationStatus = "Please fix the errors in the form."
            return
        }

        isLoading = true
        registrationStatus = null

        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    Log.d("RegistrationViewModel", "User created successfully: ${firebaseUser.uid}")

                    val ageIntValue = if (ageString.isBlank()) null else ageString.toIntOrNull()
                    val userProfile = UserRegRequest(
                        uid = firebaseUser.uid,
                        email = email.trim(),
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        age = ageIntValue,
                        password = password.trim()
                    )

                    db.collection("users").document(firebaseUser.uid)
                        .set(userProfile)
                        .await()

                    Log.d("RegistrationViewModel", "User profile saved to Firestore for UID: ${firebaseUser.uid}")
                    registrationStatus = "Registration successful for: $firstName $lastName"
                    isLoading = false
                    onRegistrationSuccess()
                } else {
                    registrationStatus = "Registration error: User was not created."
                    Log.e("RegistrationViewModel", "Firebase user is null after successful authResult.")
                    isLoading = false
                }

            } catch (e: FirebaseAuthUserCollisionException) {
                emailError = "This email is already registered."
                registrationStatus = "Registration failed: Email already exists."
                Log.w("RegistrationViewModel", "Registration failed: email already in use.", e)
                isLoading = false
            } catch (e: Exception) {
                registrationStatus = "Registration error: ${e.localizedMessage ?: "Unknown error"}"
                Log.e("RegistrationViewModel", "Registration failed with exception", e)
                isLoading = false
            }
        }
    }

    /**
     * Vymaže všetky polia formulára a resetuje chybové správy a stav registrácie.
     */
    fun clearForm() {
        email = ""
        firstName = ""
        lastName = ""
        ageString = ""
        password = ""
        confirmPassword = ""
        emailError = null
        firstNameError = null
        lastNameError = null
        ageError = null
        passwordError = null
        confirmPasswordError = null
        registrationStatus = null
        isLoading = false
    }
}