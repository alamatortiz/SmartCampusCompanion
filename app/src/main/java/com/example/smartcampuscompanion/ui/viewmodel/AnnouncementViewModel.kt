package com.example.smartcampuscompanion.ui.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.local.Announcement
import com.example.smartcampuscompanion.data.repository.AnnouncementRepository
import com.example.smartcampuscompanion.data.repository.FcmSenderRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AnnouncementViewModel(
    private val repository: AnnouncementRepository,
    private val prefs: SharedPreferences,
    private val context: Context
) : ViewModel() {

    private val fcmSender = FcmSenderRepository(context)

    // CRITICAL FIX: Use Firebase Auth email directly, not prefs
    private val currentUserEmail: String
        get() = FirebaseAuth.getInstance().currentUser?.email?.lowercase()
            ?: prefs.getString("user_email", "guest")?.lowercase()
            ?: "guest"

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _readIds = MutableStateFlow<Set<String>>(emptySet())

    private val _currentFilter = MutableStateFlow("All")
    val currentFilter: StateFlow<String> = _currentFilter.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val announcements: StateFlow<List<Announcement>> = repository.getAnnouncements()
        .combine(_readIds) { list, readIds ->
            list.map { announcement ->
                val key = announcement.documentId.ifEmpty { announcement.id.toString() }
                announcement.copy(isRead = readIds.contains(key))
            }
        }
        .combine(_currentFilter) { list, filter ->
            when (filter) {
                "Read" -> list.filter { it.isRead }
                "Unread" -> list.filter { !it.isRead }
                else -> list
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        android.util.Log.d("AnnouncementVM", "ViewModel CREATED for user: $currentUserEmail")
        loadReadStatusForCurrentUser()
    }

    fun loadReadStatusForCurrentUser() {
        val readKey = "read_announcements_$currentUserEmail"
        val saved = prefs.getStringSet(readKey, emptySet())?.toSet() ?: emptySet()
        _readIds.value = saved
        android.util.Log.d("AnnouncementVM", "LOADED from $readKey: $saved")
    }

    fun setFilter(filter: String) {
        _currentFilter.value = filter
    }

    fun markAsRead(announcement: Announcement) {
        val readKey = "read_announcements_$currentUserEmail"
        val key = announcement.documentId.ifEmpty { announcement.id.toString() }

        val currentSet = prefs.getStringSet(readKey, emptySet())?.toSet() ?: emptySet()
        val updated = currentSet.toMutableSet().apply { add(key) }

        _readIds.value = updated

        // Force commit to disk immediately
        prefs.edit().putStringSet(readKey, updated).commit()

        android.util.Log.d("AnnouncementVM", "SAVED to $readKey: $updated")
    }

    fun postAnnouncement(title: String, content: String) {
        if (title.isBlank() || content.isBlank()) {
            _error.value = "Title and content cannot be empty"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            repository.postAnnouncement(title, content)
                .onSuccess {
                    _error.value = null
                    fcmSender.sendAnnouncementToTopic(title, content)
                        .onSuccess { android.util.Log.d("FCM", "Broadcast sent") }
                        .onFailure { android.util.Log.e("FCM", "Broadcast failed: ${it.message}") }
                }
                .onFailure {
                    _error.value = it.message ?: "Failed to post"
                }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        android.util.Log.d("AnnouncementVM", "ViewModel DESTROYED for user: $currentUserEmail")
    }
}