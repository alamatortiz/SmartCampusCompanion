package com.example.smartcampuscompanion.service

import android.util.Log
import com.example.smartcampuscompanion.util.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val prefs = getSharedPreferences("session", MODE_PRIVATE)
        if (!prefs.getBoolean("notifications_enabled", true)) {
            Log.d("FCM", "Notifications disabled by user")
            return
        }

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Campus Announcement"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "New update available"

        Log.d("FCM", "Received: $title - $body")
        NotificationHelper.showAnnouncementAlert(this, title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
        // Store token locally (optional, for server integration later)
        getSharedPreferences("fcm", MODE_PRIVATE).edit().putString("token", token).apply()
    }
}