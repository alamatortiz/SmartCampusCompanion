package com.example.smartcampuscompanion.data.local

data class Task(
    val id: Int = 0,
    val documentId: String = "",
    val userId: String = "",
    val title: String,
    val description: String,
    val dueDate: Long,
    val isCompleted: Boolean = false
)