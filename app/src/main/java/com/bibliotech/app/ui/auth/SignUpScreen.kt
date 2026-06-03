package com.bibliotech.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import com.bibliotech.app.R
import com.bibliotech.app.ui.components.BibliotechAuthCard
import com.bibliotech.app.ui.components.BibliotechAuthTextField
import com.bibliotech.app.ui.components.BibliotechAuthTitle
import com.bibliotech.app.ui.components.BibliotechPasswordStrength
import com.bibliotech.app.ui.components.BibliotechPrimaryButton
import com.bibliotech.app.ui.components.BibliotechSecondaryButton

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    val strength = when {
        password.length >= 12 -> 4
        password.length >= 8 -> 3
        password.length >= 6 -> 2
        password.isNotBlank() -> 1
        else -> 0
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BibliotechAuthCard(modifier = Modifier.fillMaxWidth()) {
            BibliotechAuthTitle(
                title = stringResource(id = R.string.auth_signup),
                subtitle = stringResource(id = R.string.auth_signup_subtitle)
            )

            BibliotechAuthTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = stringResource(id = R.string.auth_full_name),
                placeholder = "Your name"
            )

            BibliotechAuthTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(id = R.string.auth_email),
                placeholder = "name@example.com"
            )

            BibliotechAuthTextField(
                value = password,
                onValueChange = { password = it },
                label = stringResource(id = R.string.auth_password),
                isPassword = true,
                placeholder = "Create a strong password"
            )

            BibliotechPasswordStrength(score = strength)

            BibliotechAuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = stringResource(id = R.string.auth_confirm_password),
                isPassword = true,
                placeholder = "Repeat password"
            )

            val activity = LocalContext.current as ComponentActivity
            val authViewModel: AuthViewModel = viewModel(activity)
            val uiState by authViewModel.uiState.collectAsState()

            LaunchedEffect(uiState.userId) {
                if (uiState.userId != null) onSuccess()
            }

            BibliotechPrimaryButton(
                text = if (uiState.isLoading) stringResource(id = R.string.auth_loading) else stringResource(id = R.string.auth_continue),
                enabled = !uiState.isLoading,
                onClick = {
                    if (password != confirmPassword) {
                        authViewModel.showError("Passwords do not match")
                    } else {
                        authViewModel.signUp(fullName, email, password)
                    }
                }
            )

            uiState.error?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            BibliotechSecondaryButton(
                text = stringResource(id = R.string.auth_login),
                onClick = onLoginClick
            )

            BibliotechSecondaryButton(
                text = stringResource(id = R.string.auth_back),
                onClick = onBackClick
            )
        }
    }
}