package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.local.Announcement
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AnnouncementRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("announcements")

    fun getAnnouncements(): Flow<List<Announcement>> = callbackFlow {
        val listener = collection
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    val title = doc.getString("title") ?: return@mapNotNull null
                    val content = doc.getString("content") ?: return@mapNotNull null
                    val date = doc.getLong("date") ?: System.currentTimeMillis()
                    Announcement(
                        id = doc.id.hashCode(),
                        documentId = doc.id,
                        title = title,
                        content = content,
                        date = date,
                        isRead = false
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun postAnnouncement(title: String, content: String): Result<Unit> {
        return try {
            val data = hashMapOf(
                "title" to title,
                "content" to content,
                "date" to System.currentTimeMillis()
            )
            collection.add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}