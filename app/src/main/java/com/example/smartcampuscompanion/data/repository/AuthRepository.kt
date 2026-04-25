package com.example.smartcampuscompanion.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String,
    val name: String,
    val email: String,
    val role: String,
    val department: String,
    val studentId: String
)

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    val currentUser get() = auth.currentUser

    suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val uid = currentUser?.uid ?: throw Exception("Login succeeded but no user")
            getUserProfile(uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, name: String, studentId: String, department: String): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )?.await()

            // Create Firestore user profile
            result.user?.uid?.let { uid ->
                val userData = hashMapOf(
                    "studentId" to studentId,
                    "name" to name,
                    "email" to email,
                    "department" to department,
                    "role" to "student",
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("users").document(uid).set(userData).await()
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String? = currentUser?.uid): Result<UserProfile> {
        return try {
            val userId = uid ?: throw Exception("No authenticated user")
            val doc = db.collection("users").document(userId).get().await()
            val email = currentUser?.email ?: doc.getString("email") ?: ""
            val role = doc.getString("role") ?: if (email.lowercase() == ADMIN_EMAIL) "admin" else "student"
            Result.success(
                UserProfile(
                    uid = userId,
                    name = doc.getString("name") ?: currentUser?.displayName ?: email.substringBefore("@"),
                    email = email,
                    role = role,
                    department = doc.getString("department") ?: "",
                    studentId = doc.getString("studentId") ?: ""
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()
    fun isAdmin(): Boolean = currentUser?.email?.lowercase() == ADMIN_EMAIL

    companion object {
        const val ADMIN_EMAIL = "admin@campus.edu"
    }
}