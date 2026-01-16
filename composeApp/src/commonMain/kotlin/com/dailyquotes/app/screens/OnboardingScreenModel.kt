package com.dailyquotes.app.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dailyquotes.shared.NotificationManager
import com.dailyquotes.shared.UserPreferences
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingScreenModel(
    private val userPreferences: UserPreferences,
    private val notificationManager: NotificationManager
) : StateScreenModel<OnboardingScreenModel.State>(State()) {

    data class State(
        val selectedHour: Int = 9,
        val selectedMinute: Int = 0,
        val isCompleting: Boolean = false
    )

    fun updateHour(hour: Int) {
        mutableState.update { it.copy(selectedHour = hour) }
    }

    fun updateMinute(minute: Int) {
        mutableState.update { it.copy(selectedMinute = minute) }
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        screenModelScope.launch {
            mutableState.update { it.copy(isCompleting = true) }

            try {
                // Save notification time to preferences
                userPreferences.setNotificationTime(
                    hour = state.value.selectedHour,
                    minute = state.value.selectedMinute
                )

                // Schedule daily notification
                notificationManager.scheduleDailyReminder(
                    hour = state.value.selectedHour,
                    minute = state.value.selectedMinute
                )

                // Mark onboarding as complete
                userPreferences.setHasShownSplash(true)

                // Notify completion
                onComplete()
            } catch (e: Exception) {
                // If something goes wrong, still mark as complete to prevent getting stuck
                userPreferences.setHasShownSplash(true)
                onComplete()
            }
        }
    }
}
