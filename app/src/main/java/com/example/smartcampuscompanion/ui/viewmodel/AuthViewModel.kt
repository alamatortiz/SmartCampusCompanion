package com.example.smartcampuscompanion.ui.viewmodel

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val prefs: SharedPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.login(email, password)
                .onSuccess { profile ->
                    // Use the email from Firebase Auth, not profile
                    val authEmail = authRepository.currentUser?.email?.lowercase() ?: email.lowercase()
                    prefs.edit {
                        putBoolean("logged_in", true)
                        putString("user_name", profile.name)
                        putString("user_role", profile.role)
                        putString("user_email", authEmail)  // CRITICAL: use auth email
                        putString("user_department", profile.department)
                        putString("user_student_id", profile.studentId)
                    }
                    _authState.value = AuthState.Success(profile.role == "admin")
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Authentication failed")
                }
        }
    }

    fun register(email: String, password: String, name: String, studentId: String, department: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.register(email, password, name, studentId, department)
                .onSuccess {
                    _authState.value = AuthState.Success(false)
                }
                .onFailure {
                    _authState.value = AuthState.Error(it.message ?: "Registration failed")
                }
        }
    }

    // AuthViewModel.kt
    fun logout() {
        authRepository.logout()

        // Do NOT use clear(). It nukes the announcement history.
        // Instead, remove only the login-related keys.
        prefs.edit {
            remove("logged_in")
            remove("user_name")
            remove("user_role")
            remove("user_email")
            remove("user_department")
            remove("user_student_id")
            remove("user_id")
        }
        _authState.value = AuthState.Idle
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val isAdmin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}