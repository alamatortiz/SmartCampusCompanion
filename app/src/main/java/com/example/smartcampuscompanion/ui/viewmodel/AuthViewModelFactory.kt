package com.example.smartcampuscompanion.ui.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartcampuscompanion.data.repository.AuthRepository

class AuthViewModelFactory(
    private val authRepository: AuthRepository,
    private val prefs: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository, prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}