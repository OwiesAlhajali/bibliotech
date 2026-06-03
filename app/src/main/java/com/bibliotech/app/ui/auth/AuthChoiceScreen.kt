package com.bibliotech.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bibliotech.app.R
import com.bibliotech.app.ui.components.BibliotechAuthCard
import com.bibliotech.app.ui.components.BibliotechAuthChip
import com.bibliotech.app.ui.components.BibliotechAuthTitle
import com.bibliotech.app.ui.components.BibliotechDividerWithText
import com.bibliotech.app.ui.components.BibliotechPrimaryButton
import com.bibliotech.app.ui.components.BibliotechSecondaryButton
import com.bibliotech.app.ui.splash.BibliotechLogo
import com.bibliotech.app.ui.theme.Gold
import com.bibliotech.app.ui.theme.Navy
import com.bibliotech.app.ui.theme.SoftGray

@Composable
fun AuthChoiceScreen(
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onGuestClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Navy, Color(0xFF1E293B), Navy)
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .background(Gold.copy(alpha = 0.12f), shape = MaterialTheme.shapes.extraLarge),
                contentAlignment = Alignment.Center
            ) {
                BibliotechLogo(modifier = Modifier.size(72.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(id = R.string.splash_tagline),
                style = MaterialTheme.typography.bodyMedium,
                color = SoftGray
            )

            Spacer(modifier = Modifier.height(28.dp))

            BibliotechAuthCard(modifier = Modifier.fillMaxWidth()) {
                BibliotechAuthTitle(
                    title = stringResource(id = R.string.auth_welcome_title),
                    subtitle = stringResource(id = R.string.auth_welcome_subtitle)
                )

                BibliotechPrimaryButton(
                    text = stringResource(id = R.string.auth_login),
                    onClick = onLoginClick
                )

                BibliotechSecondaryButton(
                    text = stringResource(id = R.string.auth_signup),
                    onClick = onSignUpClick
                )

                BibliotechDividerWithText(text = stringResource(id = R.string.auth_or_continue_with))

                BibliotechAuthChip(
                    text = stringResource(id = R.string.auth_google),
                    modifier = Modifier.fillMaxWidth()
                )

                BibliotechAuthChip(
                    text = stringResource(id = R.string.auth_apple),
                    modifier = Modifier.fillMaxWidth()
                )

                BibliotechSecondaryButton(
                    text = stringResource(id = R.string.auth_guest),
                    onClick = onGuestClick
                )
            }
        }
    }
}