package com.example.smartcampuscompanion.data.local

data class Announcement(
    val id: Int = 0,
    val documentId: String = "",
    val title: String,
    val content: String,
    val date: Long,
    val isRead: Boolean = false
)