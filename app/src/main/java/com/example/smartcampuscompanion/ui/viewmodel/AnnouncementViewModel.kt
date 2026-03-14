package com.example.smartcampuscompanion.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcampuscompanion.data.local.Announcement
import com.example.smartcampuscompanion.data.local.AnnouncementDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AnnouncementViewModel(private val announcementDao: AnnouncementDao) : ViewModel() {

    // 1. Observe the announcements as a StateFlow
    val allAnnouncements: StateFlow<List<Announcement>> = announcementDao.getAllAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. The init block runs as soon as the ViewModel is created
    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        viewModelScope.launch {
            // We only seed if the database is currently empty
            announcementDao.getAllAnnouncements().collect { list ->
                if (list.isEmpty()) {
                    val fixedNotices = listOf(
                        Announcement(
                            title = "Midterm Examination Schedule",
                            content = "Please be informed that Midterms start on March 1st. Check your student portal for room assignments.",
                            date = System.currentTimeMillis()
                        ),
                        Announcement(
                            title = "Library Extended Hours",
                            content = "To support your study sessions, the Central Library will remain open until 10:00 PM this week.",
                            date = System.currentTimeMillis()
                        ),
                        Announcement(
                            title = "System Maintenance",
                            content = "The Campus Wi-Fi will undergo maintenance this Sunday from 2:00 AM to 4:00 AM.",
                            date = System.currentTimeMillis()
                        )
                    )
                    fixedNotices.forEach { announcementDao.insertAnnouncement(it) }
                }
            }
        }
    }

    fun markAsRead(announcement: Announcement) {
        viewModelScope.launch {
            announcementDao.updateAnnouncement(announcement.copy(isRead = true))
        }
    }
}