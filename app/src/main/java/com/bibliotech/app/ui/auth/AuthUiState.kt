package com.bibliotech.app.ui.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId: String? = null
)
