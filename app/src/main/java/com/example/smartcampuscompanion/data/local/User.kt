package com.example.smartcampuscompanion.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val studentId: String,
    val name: String,
    val password: String,
    val department: String
)