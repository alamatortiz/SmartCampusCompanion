package com.example.smartcampuscompanion.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnouncementDao {
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<Announcement>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: Announcement)

    @Update
    suspend fun updateAnnouncement(announcement: Announcement)

    @Query("SELECT * FROM announcements WHERE isRead = :isRead ORDER BY date DESC")
    fun getAnnouncementsByStatus(isRead: Boolean): Flow<List<Announcement>>

}