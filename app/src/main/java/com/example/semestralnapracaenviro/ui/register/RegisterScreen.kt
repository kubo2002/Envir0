package com.example.semestralnapracaenviro.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.semestralnapracaenviro.navigation.ScreenRoute
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.semestralnapracaenviro.viewmodels.RegViewModel

@Composable
fun RegisterScreen (
    navController: NavController,
    regViewModel: RegViewModel = viewModel()

) {
    val context = LocalContext.current
    LaunchedEffect(regViewModel.registrationStatus) {
        val status = regViewModel.registrationStatus
        if (status != null) {
            Toast.makeText(context, status, Toast.LENGTH_LONG).show()
            if (status.startsWith("Registrácia úspešná")) {
                navController.navigate(ScreenRoute.MAINSCREEN.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
                regViewModel.clearForm()
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        Text("Vytvoriť účet", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Krstné meno
        OutlinedTextField(
            value = regViewModel.firstName,
            onValueChange = { regViewModel.firstName = it },
            label = { Text("Krstné meno") },
            modifier = Modifier.fillMaxWidth(),
            isError = regViewModel.firstNameError != null,
            singleLine = true
        )
        regViewModel.firstNameError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Priezvisko
        OutlinedTextField(
            value = regViewModel.lastName,
            onValueChange = { regViewModel.lastName = it },
            label = { Text("Priezvisko") },
            modifier = Modifier.fillMaxWidth(),
            isError = regViewModel.lastNameError != null,
            singleLine = true
        )
        regViewModel.lastNameError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))


        // Email
        OutlinedTextField(
            value = regViewModel.email,
            onValueChange = { regViewModel.email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            isError = regViewModel.emailError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        regViewModel.emailError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Vek (voliteľné)
        OutlinedTextField(
            value = regViewModel.ageString,
            onValueChange = { regViewModel.ageString = it },
            label = { Text("Vek (voliteľné)") },
            modifier = Modifier.fillMaxWidth(),
            isError = regViewModel.ageError != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        regViewModel.ageError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))


        // Heslo
        OutlinedTextField(
            value = regViewModel.password,
            onValueChange = { regViewModel.password = it },
            label = { Text("Heslo") },
            modifier = Modifier.fillMaxWidth(),
            isError = regViewModel.passwordError != null,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        regViewModel.passwordError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Potvrdenie hesla
        OutlinedTextField(
            value = regViewModel.confirmPassword,
            onValueChange = { regViewModel.confirmPassword = it },
            label = { Text("Potvrdiť heslo") },
            modifier = Modifier.fillMaxWidth(),
            isError = regViewModel.confirmPasswordError != null,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        regViewModel.confirmPasswordError?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(24.dp))


        // Tlačidlo Registrovať
        Button(
            onClick = {
                regViewModel.registerUser {

                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !regViewModel.isLoading
        ) {
            if (regViewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Registrovať")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))


        TextButton(onClick = { navController.navigate(ScreenRoute.LOGIN.route) }) {
            Text("Už máte účet? Prihlásiť sa")
        }
    }

}