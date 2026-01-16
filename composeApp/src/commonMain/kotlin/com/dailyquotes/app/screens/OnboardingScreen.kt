package com.dailyquotes.app.screens

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // App Title
                Text(
                    text = "Daily Quotes",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Informational Text
                Text(
                    text = "Choose when you'd like to receive your daily quote.",
                    fontSize = 18.sp,
                    color = Color(0xFFBBBBBB),
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )

                Text(
                    text = "If not set, quotes will be delivered at 9 AM every day.",
                    fontSize = 14.sp,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Time Picker
                TimePickerComponent(
                    hour = state.selectedHour,
                    minute = state.selectedMinute,
                    onHourChange = { screenModel.updateHour(it) },
                    onMinuteChange = { screenModel.updateMinute(it) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "You can change this later in settings.",
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Get Started Button
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
                        .height(56.dp)
                ) {
                    if (state.isCompleting) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Get Started",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
