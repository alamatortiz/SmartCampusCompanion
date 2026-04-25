package com.example.smartcampuscompanion.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.smartcampuscompanion.util.NotificationHelper

class TaskAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("task_title") ?: "Task"
        val taskId = intent.getIntExtra("task_id", 0)

        Log.d("TaskAlarmReceiver", "Alarm fired for: $taskTitle (id: $taskId)")

        val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        val notificationsEnabled = prefs.getBoolean("notifications_enabled", true)

        Log.d("TaskAlarmReceiver", "Notifications enabled: $notificationsEnabled")

        if (!notificationsEnabled) {
            Log.d("TaskAlarmReceiver", "Notifications disabled - skipping")
            return
        }

        Log.d("TaskAlarmReceiver", "Showing notification for: $taskTitle")
        NotificationHelper.showTaskReminder(context, taskTitle, taskId)
        Toast.makeText(context, "Reminder: $taskTitle", Toast.LENGTH_LONG).show()
    }
}