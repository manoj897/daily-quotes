package com.dailyquotes.app.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dailyquotes.app.components.TimePickerComponent

@OptIn(ExperimentalMaterial3Api::class)
class SettingsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<SettingsScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { padding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .navigationBarsPadding()
            ) {
                val compactHeight = maxHeight < 620.dp
                val outerPadding = if (compactHeight) 16.dp else 24.dp
                val pickerHeight = (maxHeight * 0.28f).coerceIn(128.dp, 180.dp)
                val pickerWidth = (maxWidth * 0.22f).coerceIn(72.dp, 88.dp)
                val pickerItemHeight = (pickerHeight / 5f).coerceIn(28.dp, 38.dp)
                val pickerGap = (maxWidth * 0.045f).coerceIn(16.dp, 32.dp)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = outerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(if (compactHeight) 8.dp else 12.dp)
                        ) {
                            // Section Title
                            Text(
                                text = "Daily Quote Notification",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "Choose when you'd like to receive your daily quote notification.",
                                fontSize = 14.sp,
                                color = Color(0xFFBBBBBB)
                            )
                        }

                        // Time Picker
                        TimePickerComponent(
                            hour = state.hour,
                            minute = state.minute,
                            onHourChange = { screenModel.updateHour(it) },
                            onMinuteChange = { screenModel.updateMinute(it) },
                            pickerWidth = pickerWidth,
                            pickerHeight = pickerHeight,
                            itemHeight = pickerItemHeight,
                            columnGap = pickerGap,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Save Reminder Button
                    Button(
                        onClick = { screenModel.saveSettings() },
                        enabled = !state.isSaving,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                    ) {
                        if (state.isSaving) {
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

                // Success Message
                AnimatedVisibility(
                    visible = state.feedbackMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = outerPadding + 72.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = state.feedbackMessage ?: "",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(
                                horizontal = 24.dp,
                                vertical = 12.dp
                            )
                        )
                    }
                }
            }
        }
    }
}
