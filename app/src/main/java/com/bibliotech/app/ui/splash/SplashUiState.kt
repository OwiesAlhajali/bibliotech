package com.bibliotech.app.ui.splash

data class SplashUiState(
    val isLogoVisible: Boolean = false,
    val isTitleVisible: Boolean = false,
    val isTaglineVisible: Boolean = false,
    val shouldNavigate: Boolean = false
)