package com.dailyquotes.shared

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

actual class NotificationManager(private val context: Context) {

    actual fun scheduleDailyReminder(hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check if we can schedule exact alarms on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Cannot schedule exact alarms - permissions not granted
                return
            }
        }

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("HOUR", hour)
            putExtra("MINUTE", minute)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // Use setExactAndAllowWhileIdle for reliable delivery even during Doze mode
        // This doesn't repeat automatically, so we reschedule in the receiver
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        // Store the schedule time in SharedPreferences for boot receiver
        val prefs = context.getSharedPreferences("daily_quotes_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putInt("reminder_hour", hour)
            putInt("reminder_minute", minute)
            putBoolean("reminder_enabled", true)
            apply()
        }
    }

    actual fun cancelAllReminders() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)

        // Clear the schedule from SharedPreferences
        val prefs = context.getSharedPreferences("daily_quotes_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("reminder_enabled", false)
            apply()
        }
    }
}
