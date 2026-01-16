package com.dailyquotes.app.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dailyquotes.shared.NotificationManager
import com.dailyquotes.shared.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SettingsScreenModel(
    private val userPreferences: UserPreferences,
    private val notificationManager: NotificationManager
) : StateScreenModel<SettingsScreenModel.State>(
    State(
        hour = userPreferences.getNotificationHour(),
        minute = userPreferences.getNotificationMinute()
    )
) {

    data class State(
        val hour: Int,
        val minute: Int,
        val isSaving: Boolean = false,
        val feedbackMessage: String? = null
    )

    fun updateHour(hour: Int) {
        mutableState.update { it.copy(hour = hour) }
    }

    fun updateMinute(minute: Int) {
        mutableState.update { it.copy(minute = minute) }
    }

    private fun formatTime(hour: Int, minute: Int): String {
        // 24-hour format: "09:00" or "14:30"
        return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
    }

    fun saveSettings() {
        screenModelScope.launch {
            mutableState.update { it.copy(isSaving = true, feedbackMessage = null) }

            try {
                // Save notification time to preferences
                userPreferences.setNotificationTime(
                    hour = state.value.hour,
                    minute = state.value.minute
                )

                // Schedule notification (FLAG_UPDATE_CURRENT will update existing alarm)
                notificationManager.scheduleDailyReminder(
                    hour = state.value.hour,
                    minute = state.value.minute
                )

                // Calculate when next notification fires
                val now = Clock.System.now()
                val timezone = TimeZone.currentSystemDefault()
                val currentDateTime = now.toLocalDateTime(timezone)

                val selectedTime = LocalTime(state.value.hour, state.value.minute)
                val currentTime = currentDateTime.time

                val timeStr = formatTime(state.value.hour, state.value.minute)
                val message = if (selectedTime > currentTime) {
                    "Saved! Next notification at $timeStr today"
                } else {
                    "Saved! Next notification at $timeStr tomorrow"
                }

                // Show success message with context
                mutableState.update { it.copy(isSaving = false, feedbackMessage = message) }

                // Hide success message after 3 seconds
                delay(3000)
                mutableState.update { it.copy(feedbackMessage = null) }
            } catch (e: Exception) {
                // If something goes wrong, still update state
                mutableState.update { it.copy(isSaving = false, feedbackMessage = null) }
            }
        }
    }
}
