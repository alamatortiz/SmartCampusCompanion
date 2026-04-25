package com.example.smartcampuscompanion.data.repository

import com.example.smartcampuscompanion.data.local.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("tasks")

    fun getTasksForUser(userId: String): Flow<List<Task>> = callbackFlow {
        val listener = collection
            .whereEqualTo("userId", userId)
            // REMOVED: .orderBy("dueDate", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    Task(
                        id = doc.id.hashCode(),
                        documentId = doc.id,
                        userId = doc.getString("userId") ?: "",
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        dueDate = doc.getLong("dueDate") ?: 0L,
                        isCompleted = doc.getBoolean("isCompleted") ?: false
                    )
                }?.sortedBy { it.dueDate } ?: emptyList() // SORT HERE INSTEAD
                trySend(tasks)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addTask(task: Task): Result<String> {
        return try {
            val data = hashMapOf(
                "userId" to task.userId,
                "title" to task.title,
                "description" to task.description,
                "dueDate" to task.dueDate,
                "isCompleted" to task.isCompleted,
                "createdAt" to System.currentTimeMillis()
            )
            val docRef = collection.add(data).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            if (task.documentId.isEmpty()) return Result.failure(Exception("Invalid task ID"))
            collection.document(task.documentId).update(
                mapOf(
                    "title" to task.title,
                    "description" to task.description,
                    "dueDate" to task.dueDate,
                    "isCompleted" to task.isCompleted
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTask(documentId: String): Result<Unit> {
        return try {
            collection.document(documentId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}