package com.example.smartcampuscompanion.data.repository

import android.content.Context
import android.util.Log
import com.example.smartcampuscompanion.R
import com.google.auth.oauth2.GoogleCredentials
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream

class FcmSenderRepository(private val context: Context) {

    // TODO: Replace with YOUR Firebase project ID
    private val projectId = "smart-campus-companion-1e007"

    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
        install(Logging)
    }

    suspend fun sendAnnouncementToTopic(title: String, body: String): Result<Unit> {
        return try {
            val accessToken = getAccessToken()

            val message = FcmV1Message(
                message = TopicMessage(
                    topic = "announcements",
                    notification = NotificationPayload(title = title, body = body),
                    data = mapOf("type" to "announcement", "title" to title, "body" to body),
                    android = AndroidConfig(
                        priority = "high",
                        notification = AndroidNotification(channelId = "smart_campus_channel", sound = "default")
                    )
                )
            )

            val response: FcmV1Response = httpClient.post(
                "https://fcm.googleapis.com/v1/projects/$projectId/messages:send"
            ) {
                headers {
                    append("Authorization", "Bearer $accessToken")
                }
                contentType(ContentType.Application.Json)
                setBody(message)
            }.body()

            if (response.name.isNullOrBlank()) {
                throw Exception("FCM returned empty response")
            }

            Log.d("FcmSender", "FCM broadcast sent: ${response.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FcmSender", "FCM broadcast failed", e)
            Result.failure(e)
        }
    }

    private suspend fun getAccessToken(): String = withContext(Dispatchers.IO) {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.service_account)
        val credentials = GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        credentials.refreshIfExpired()
        credentials.accessToken.tokenValue
    }
}

@Serializable
data class FcmV1Message(val message: TopicMessage)

@Serializable
data class TopicMessage(
    val topic: String,
    val notification: NotificationPayload,
    val data: Map<String, String>,
    val android: AndroidConfig
)

@Serializable
data class NotificationPayload(val title: String, val body: String)

@Serializable
data class AndroidConfig(val priority: String, val notification: AndroidNotification)

@Serializable
data class AndroidNotification(val channelId: String, val sound: String)

@Serializable
data class FcmV1Response(val name: String? = null)