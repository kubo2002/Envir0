package com.example.semestralnapracaenviro.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.text.isBlank
import kotlin.text.isNotBlank
import kotlin.text.toIntOrNull
import kotlin.text.trim
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.semestralnapracaenviro.data.model.UserRegRequest

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

    var registrationStatus by mutableStateOf<String?>(null) // Správa o úspechu alebo všeobecnej chybe
    var isLoading by mutableStateOf(false)

    // Inštancie Firebase služieb
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    /**
     * Validuje vstupné polia formulára.
     * @return true ak sú všetky povinné polia platné, inak false.
     */
    private fun validateInputs(): Boolean {
        // Reset predchádzajúcich chýb
        emailError = null
        firstNameError = null
        lastNameError = null
        ageError = null
        passwordError = null
        confirmPasswordError = null
        var isValid = true

        if (firstName.isBlank()) {
            firstNameError = "Krstné meno je povinné"
            isValid = false
        }

        if (lastName.isBlank()) {
            lastNameError = "Priezvisko je povinné"
            isValid = false
        }

        if (email.isBlank()) {
            emailError = "Email je povinný"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Neplatný formát emailu"
            isValid = false
        }

        if (ageString.isNotBlank()) {
            val ageValue = ageString.toIntOrNull()
            if (ageValue == null || ageValue <= 0 || ageValue > 120) {
                ageError = "Neplatný vek"
                isValid = false
            }
        }

        if (password.isBlank()) {
            passwordError = "Heslo je povinné"
            isValid = false
        } else if (password.length < 6) {
            passwordError = "Heslo musí mať aspoň 6 znakov"
            isValid = false
        }

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Potvrdenie hesla je povinné"
            isValid = false
        } else if (password != confirmPassword) {
            confirmPasswordError = "Heslá sa nezhodujú"
            isValid = false
        }

        return isValid
    }


    /**
     * Registruje nového používateľa pomocou emailu a hesla
     * a ukladá jeho profilové dáta do Firestore.
     * @param onRegistrationSuccess Lambda funkcia, ktorá sa zavolá po úspešnej registrácii.
     */
    fun registerUser(onRegistrationSuccess: () -> Unit) {
        if (!validateInputs()) {
            registrationStatus = "Prosím, opravte chyby vo formulári."
            return
        }

        isLoading = true
        registrationStatus = null // Reset predchádzajúceho stavu

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
                        .await() // Počkáme na dokončenie operácie

                    Log.d("RegistrationViewModel", "User profile saved to Firestore for UID: ${firebaseUser.uid}")
                    registrationStatus = "Registrácia úspešná pre: $firstName $lastName"
                    isLoading = false
                    onRegistrationSuccess() // Zavolanie callbacku po úspechu

                } else {
                    // Tento prípad by nemal nastať, ak createUserWithEmailAndPassword bol úspešný bez výnimky
                    registrationStatus = "Chyba pri registrácii: Používateľ nebol vytvorený."
                    Log.e("RegistrationViewModel", "Firebase user is null after successful authResult.")
                    isLoading = false
                }

            } catch (e: FirebaseAuthUserCollisionException) {
                // Chyba: Email je už zaregistrovaný
                emailError = "Tento email je už zaregistrovaný."
                registrationStatus = "Registrácia zlyhala: Email už existuje."
                Log.w("RegistrationViewModel", "Registration failed: email already in use.", e)
                isLoading = false
            } catch (e: Exception) {
                // Iné chyby (napr. problém so sieťou, slabé heslo podľa pravidiel Firebase atď.)
                registrationStatus = "Chyba pri registrácii: ${e.localizedMessage ?: "Neznáma chyba"}"
                Log.e("RegistrationViewModel", "Registration failed with exception", e)
                isLoading = false
            }
        }
    }

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