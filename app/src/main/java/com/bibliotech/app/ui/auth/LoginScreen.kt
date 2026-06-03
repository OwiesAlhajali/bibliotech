package com.bibliotech.app.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import com.bibliotech.app.R
import com.bibliotech.app.ui.components.BibliotechAuthCard
import com.bibliotech.app.ui.components.BibliotechAuthTextField
import com.bibliotech.app.ui.components.BibliotechAuthTitle
import com.bibliotech.app.ui.components.BibliotechPasswordStrength
import com.bibliotech.app.ui.components.BibliotechPrimaryButton
import com.bibliotech.app.ui.components.BibliotechSecondaryButton

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val activity = LocalContext.current as ComponentActivity
    val authViewModel: AuthViewModel = viewModel(activity)
    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.userId) {
        if (uiState.userId != null) onSuccess()
    }

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
                title = stringResource(id = R.string.auth_login),
                subtitle = stringResource(id = R.string.auth_login_subtitle)
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
                placeholder = "••••••••"
            )

            BibliotechPasswordStrength(score = strength)

            Text(
                text = stringResource(id = R.string.auth_forgot_password),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            BibliotechPrimaryButton(
                text = if (uiState.isLoading) stringResource(id = R.string.auth_loading) else stringResource(id = R.string.auth_continue),
                enabled = !uiState.isLoading,
                onClick = { authViewModel.signIn(email, password) }
            )

            uiState.error?.let { err ->
                Text(
                    text = err,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            BibliotechSecondaryButton(
                text = stringResource(id = R.string.auth_create_account),
                onClick = onCreateAccountClick
            )

            BibliotechSecondaryButton(
                text = stringResource(id = R.string.auth_back),
                onClick = onBackClick
            )
        }
    }
}