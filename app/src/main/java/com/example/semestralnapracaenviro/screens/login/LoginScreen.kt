package com.example.semestralnapracaenviro.screens.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.semestralnapracaenviro.R
import com.example.semestralnapracaenviro.navigation.ScreenRoute

/**
 * Obrazovka pre prihlasenie používateľa.
 * Umožňuje zadanie emailu a hesla, zobrazuje chyby a indikuje priebeh prihlasovania.
 * Pri úspešnom prihlásení naviguje na hlavnú obrazovku a vyčistí formulár.
 *
 * @param navController Navigačný controller na prechod medzi obrazovkami.
 * @param loginViewModel ViewModel spravujúci stav prihlasovacieho formulára a logiku prihlásenia.
 *
 * @author Jakub Gubány
 */
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LogViewModel = viewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val loginStatus = loginViewModel.loginStatus

    LaunchedEffect(loginStatus) {
        if (loginStatus != null) {
            Toast.makeText(context, loginStatus, Toast.LENGTH_LONG).show()
            if (loginStatus.startsWith("Login successful")) {
                navController.navigate(ScreenRoute.MAINSCREEN.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
                loginViewModel.clearForm()
            }
            loginViewModel.clearLoginStatus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF388E3C)), // zelené pozadie
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.welcome_back_title),
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF2E7D32), // tmavozelená
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Email input
            OutlinedTextField(
                value = loginViewModel.email,
                onValueChange = { loginViewModel.email = it },
                placeholder = { Text(stringResource(R.string.email_input_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = loginViewModel.emailError != null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF2E7D32),
                    cursorColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password input
            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.password = it },
                placeholder = { Text(stringResource(R.string.password_input_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = loginViewModel.passwordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF2E7D32),
                    cursorColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Log In Button
            Button(
                onClick = { loginViewModel.logInUser() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(25.dp)),
                shape = RoundedCornerShape(25.dp),
                enabled = !loginViewModel.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                if (loginViewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(stringResource(R.string.log_in_button_text), color = Color.White)
                }
            }
        }
    }
}