package com.dailyquotes.shared

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Retrieve the stored reminder schedule
            val prefs = context.getSharedPreferences("daily_quotes_prefs", Context.MODE_PRIVATE)
            val reminderEnabled = prefs.getBoolean("reminder_enabled", false)

            if (reminderEnabled) {
                val hour = prefs.getInt("reminder_hour", 9)
                val minute = prefs.getInt("reminder_minute", 0)

                // Reschedule the daily reminder
                val notificationManager = NotificationManager(context)
                notificationManager.scheduleDailyReminder(hour, minute)
            }
        }
    }
}
