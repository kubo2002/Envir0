package com.example.semestralnapracaenviro.screens.register

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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.semestralnapracaenviro.R

/**
 * Composable obrazovka pre registráciu nového používateľa.
 *
 * Zobrazuje formulár pre zadanie údajov ako meno, priezvisko, vek, email, heslo a potvrdenie hesla.
 * Pri úspešnej registrácii zobrazí toast správu a presmeruje používateľa na hlavnú obrazovku.
 * Počas registrácie zobrazuje indikátor načítania.
 *
 * @param navController Navigačný kontrolér pre prepínanie medzi obrazovkami.
 * @param regViewModel ViewModel spravujúci stav formulára a logiku registrácie.
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    regViewModel: RegViewModel = viewModel()
) {
    val context = LocalContext.current

    // Spustí sa, keď sa zmení stav registrácie, zobrazí toast a pri úspechu naviguje na hlavnú obrazovku
    LaunchedEffect(regViewModel.registrationStatus) {
        regViewModel.registrationStatus?.let { status ->
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

    // Hlavné rozloženie obrazovky s pozadím a centrálne zarovnaným formulárom
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF388E3C)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // Nadpis obrazovky
            Text(
                text = stringResource(R.string.create_account_text),
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFF2E7D32),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Textové pole pre zadanie krstného mena
            OutlinedTextField(
                value = regViewModel.firstName,
                onValueChange = { regViewModel.firstName = it },
                placeholder = { Text(stringResource(R.string.first_name_input_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = regViewModel.firstNameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF2E7D32),
                    cursorColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Textové pole pre zadanie priezviska
            OutlinedTextField(
                value = regViewModel.lastName,
                onValueChange = { regViewModel.lastName = it },
                placeholder = { Text(stringResource(R.string.last_name_input_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = regViewModel.lastNameError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF2E7D32),
                    cursorColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Textové pole pre zadanie veku (číselný vstup)
            OutlinedTextField(
                value = regViewModel.ageString,
                onValueChange = { regViewModel.ageString = it },
                placeholder = { Text(stringResource(R.string.age_input_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = regViewModel.ageError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF2E7D32),
                    cursorColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Textové pole pre zadanie emailu
            OutlinedTextField(
                value = regViewModel.email,
                onValueChange = { regViewModel.email = it },
                placeholder = { Text(stringResource(R.string.email_input_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = regViewModel.emailError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF2E7D32),
                    cursorColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Textové pole pre zadanie hesla
            OutlinedTextField(
                value = regViewModel.password,
                onValueChange = { regViewModel.password = it },
                placeholder = { Text(stringResource(R.string.password_input_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = regViewModel.passwordError != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF2E7D32),
                    cursorColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Textové pole pre potvrdenie hesla
            OutlinedTextField(
                value = regViewModel.confirmPassword,
                onValueChange = { regViewModel.confirmPassword = it },
                placeholder = { Text(stringResource(R.string.confirm_password_input_placeholder)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2E7D32),
                    unfocusedBorderColor = Color(0xFF2E7D32),
                    cursorColor = Color(0xFF2E7D32)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Tlačidlo na registráciu používateľa
            Button(
                onClick = { regViewModel.registerUser {} },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .shadow(8.dp, RoundedCornerShape(25.dp)),
                shape = RoundedCornerShape(25.dp),
                enabled = !regViewModel.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                if (regViewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(stringResource(R.string.signup_button), color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontálny oddelovač s textom "or"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text(stringResource(R.string.or_text), color = Color.Gray)
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tlačidlo pre presmerovanie na prihlasovaciu obrazovku
            OutlinedButton(
                onClick = { navController.navigate(ScreenRoute.LOGIN.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32))
            ) {
                Text(stringResource(R.string.login_button))
            }
        }
    }
}