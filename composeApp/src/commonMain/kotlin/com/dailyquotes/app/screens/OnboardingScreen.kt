package com.dailyquotes.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dailyquotes.app.components.TimePickerComponent

class OnboardingScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<OnboardingScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            val compactHeight = maxHeight < 700.dp
            val outerPadding = if (compactHeight) 16.dp else 24.dp
            val pickerHeight = (maxHeight * 0.22f).coerceIn(128.dp, 180.dp)
            val pickerWidth = (maxWidth * 0.22f).coerceIn(72.dp, 88.dp)
            val pickerItemHeight = (pickerHeight / 5f).coerceIn(28.dp, 38.dp)
            val pickerGap = (maxWidth * 0.045f).coerceIn(16.dp, 32.dp)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = outerPadding)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // App Title
                    Text(
                        text = "Daily Quotes",
                        fontSize = if (compactHeight) 30.sp else 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(if (compactHeight) 8.dp else 12.dp)
                    ) {
                        // Informational Text
                        Text(
                            text = "Choose when you'd like to receive your daily quote.",
                            fontSize = if (compactHeight) 16.sp else 18.sp,
                            color = Color(0xFFBBBBBB),
                            textAlign = TextAlign.Center,
                            lineHeight = if (compactHeight) 22.sp else 26.sp
                        )

                        Text(
                            text = "If not set, quotes will be delivered at 9 AM every day.",
                            fontSize = 14.sp,
                            color = Color(0xFF888888),
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }

                    // Time Picker
                    TimePickerComponent(
                        hour = state.selectedHour,
                        minute = state.selectedMinute,
                        onHourChange = { screenModel.updateHour(it) },
                        onMinuteChange = { screenModel.updateMinute(it) },
                        pickerWidth = pickerWidth,
                        pickerHeight = pickerHeight,
                        itemHeight = pickerItemHeight,
                        columnGap = pickerGap,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "You can change this later in settings.",
                        fontSize = 12.sp,
                        color = Color(0xFF888888),
                        textAlign = TextAlign.Center
                    )
                }

                // Save Reminder Button
                Button(
                    onClick = {
                        screenModel.completeOnboarding {
                            // Navigate to QuoteScreen and clear backstack
                            navigator.replaceAll(QuoteScreen())
                        }
                    },
                    enabled = !state.isCompleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                ) {
                    if (state.isCompleting) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Save Reminder",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
