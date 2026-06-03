package com.bibliotech.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Patterns

class AuthViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signIn(email: String, password: String) {
        // basic validation
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(isLoading = false, error = "Email and password must not be empty")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _uiState.value = AuthUiState(isLoading = false, error = "Please enter a valid email address")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        _uiState.value = AuthUiState(isLoading = false, userId = uid)
                    } else {
                        _uiState.value = AuthUiState(isLoading = false, error = task.exception?.message)
                    }
                }
        } catch (e: Exception) {
            _uiState.value = AuthUiState(isLoading = false, error = e.message)
        }
    }

    fun signUp(fullName: String, email: String, password: String) {
        // basic validation
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(isLoading = false, error = "All fields are required")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _uiState.value = AuthUiState(isLoading = false, error = "Please enter a valid email address")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState(isLoading = false, error = "Password must be at least 6 characters")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        try {
            auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && fullName.isNotBlank()) {
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName.trim())
                                .build()
                            // update profile asynchronously
                            user.updateProfile(profileUpdates)
                        }
                        val uid = auth.currentUser?.uid
                        _uiState.value = AuthUiState(isLoading = false, userId = uid)
                    } else {
                        _uiState.value = AuthUiState(isLoading = false, error = task.exception?.message)
                    }
                }
        } catch (e: Exception) {
            _uiState.value = AuthUiState(isLoading = false, error = e.message)
        }
    }

    fun showError(message: String) {
        _uiState.value = _uiState.value.copy(error = message)
    }

    fun signOut() {
        viewModelScope.launch {
            auth.signOut()
            _uiState.value = AuthUiState()
        }
    }
}
