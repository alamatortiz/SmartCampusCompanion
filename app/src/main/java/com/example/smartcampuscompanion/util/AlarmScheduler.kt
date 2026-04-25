package com.example.smartcampuscompanion.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.example.smartcampuscompanion.receiver.TaskAlarmReceiver

object AlarmScheduler {

    // DEMO: Change to 30 seconds for testing. For real use, set back to 15 * 60 * 1000 (15 min)
    private const val REMINDER_OFFSET_MS = 30 * 1000 // 30 seconds before due date

    fun scheduleTaskReminder(context: Context, taskId: Int, taskTitle: String, dueDateMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmScheduler", "Cannot schedule exact alarms - permission denied")
                Toast.makeText(context, "Please allow exact alarms in Settings", Toast.LENGTH_LONG).show()
                return
            }
        }

        val intent = Intent(context, TaskAlarmReceiver::class.java).apply {
            putExtra("task_title", taskTitle)
            putExtra("task_id", taskId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = dueDateMillis - REMINDER_OFFSET_MS

        Log.d("AlarmScheduler", "Scheduling alarm for task: $taskTitle at ${java.util.Date(triggerAt)} (task due: ${java.util.Date(dueDateMillis)})")

        if (triggerAt > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent
                )
            }
            Log.d("AlarmScheduler", "Alarm scheduled successfully for $taskTitle")
            Toast.makeText(context, "Reminder set for ${taskTitle.take(20)}", Toast.LENGTH_SHORT).show()
        } else {
            Log.w("AlarmScheduler", "Trigger time is in the past - not scheduling")
            Toast.makeText(context, "Due time is too soon!", Toast.LENGTH_SHORT).show()
        }
    }

    fun cancelTaskReminder(context: Context, taskId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TaskAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        Log.d("AlarmScheduler", "Alarm cancelled for taskId: $taskId")
    }
}