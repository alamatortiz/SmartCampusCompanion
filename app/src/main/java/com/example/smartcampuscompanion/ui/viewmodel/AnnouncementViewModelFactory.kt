package com.example.smartcampuscompanion.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartcampuscompanion.data.repository.AnnouncementRepository

class AnnouncementViewModelFactory(
    private val repository: AnnouncementRepository,
    private val prefs: SharedPreferences,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AnnouncementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnnouncementViewModel(repository, prefs, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}