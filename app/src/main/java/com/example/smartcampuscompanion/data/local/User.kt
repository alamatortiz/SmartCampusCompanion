package com.example.smartcampuscompanion.data.local

data class User(
    val studentId: String = "",
    val name: String = "",
    val email: String = "",
    val department: String = "",
    val role: String = "student"
)