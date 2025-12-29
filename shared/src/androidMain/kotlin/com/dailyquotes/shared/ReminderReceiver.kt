package com.dailyquotes.shared

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager as AndroidNotificationManager
import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Show the notification
        showNotification(context)

        // Reschedule the next alarm for tomorrow
        rescheduleNextAlarm(context, intent)
    }

    private fun showNotification(context: Context) {
        val channelId = "daily_quote_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as AndroidNotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Quotes",
                AndroidNotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily motivational quotes and reflections"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // We assume MainActivity is the entry point
        val activityIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(context, channelId)
        } else {
            Notification.Builder(context)
        }

        val notification = builder
            .setContentTitle("Daily Inspiration")
            .setContentText("Time for your daily quote reflection.")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Placeholder icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun rescheduleNextAlarm(context: Context, intent: Intent) {
        val hour = intent.getIntExtra("HOUR", 9)
        val minute = intent.getIntExtra("MINUTE", 0)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check if we can schedule exact alarms on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                return
            }
        }

        val nextIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("HOUR", hour)
            putExtra("MINUTE", minute)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule for tomorrow at the same time
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            add(Calendar.DATE, 1) // Add one day
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
