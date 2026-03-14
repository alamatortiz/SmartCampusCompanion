package com.example.smartcampuscompanion.data.local

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User)

    @Query("SELECT * FROM users WHERE studentId = :id AND password = :pass LIMIT 1")
    suspend fun login(id: String, pass: String): User?
}