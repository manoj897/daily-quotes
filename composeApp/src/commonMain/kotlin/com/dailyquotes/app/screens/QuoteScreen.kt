package com.dailyquotes.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.focus.onFocusChanged
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dailyquotes.app.components.SourceShareToggle
import com.dailyquotes.app.components.ZenQuotesAttribution
import com.dailyquotes.app.components.appendZenQuotesSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class QuoteScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<QuoteScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        
        var showShareSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { navigator.push(ReflectionsScreen()) }) {
                            Icon(Icons.Default.List, contentDescription = "Reflections")
                        }
                        IconButton(onClick = { navigator.push(SettingsScreen()) }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val currentState = state) {
                    is QuoteScreenModel.State.Loading -> CircularProgressIndicator(color = Color.White)
                    is QuoteScreenModel.State.Error -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                currentState.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Check your connection, then retry.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { screenModel.fetchQuote() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Retry")
                                Spacer(Modifier.width(8.dp))
                                Text("Retry")
                            }
                        }
                    }
                    is QuoteScreenModel.State.Success -> {
                        val quote = currentState.quote
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "\"",
                                fontSize = 80.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = quote.q,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Light,
                                textAlign = TextAlign.Center,
                                lineHeight = 36.sp
                            )
                            Text(
                                text = "- ${quote.a}",
                                fontSize = 16.sp,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier.padding(top = 16.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )
                            ZenQuotesAttribution(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.Center
                            )
                            
                            Spacer(Modifier.height(48.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { navigator.push(ReflectionScreen(quote)) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("REFLECT")
                                }

                                OutlinedButton(
                                    onClick = { showShareSheet = true },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.primary
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("SHARE")
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (showShareSheet && state is QuoteScreenModel.State.Success) {
            val quote = (state as QuoteScreenModel.State.Success).quote
            ModalBottomSheet(
                onDismissRequest = { showShareSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF111111),
                contentColor = Color.White
            ) {
                var userTake by remember { mutableStateOf("") }
                var includeSourceInShare by remember { mutableStateOf(true) }
                var isUserTakeHidden by remember { mutableStateOf(true) }
                var isUserTakeFocused by remember { mutableStateOf(false) }
                val hasUserTake = userTake.isNotBlank()
                val isUserTakeActive = !isUserTakeHidden && (hasUserTake || isUserTakeFocused)
                fun shareQuote() {
                    val shareText = buildString {
                        append("\"${quote.q}\"")
                        append("\n— ${quote.a}")
                        if (!isUserTakeHidden && hasUserTake) {
                            append("\n\nMy Take\n$userTake")
                        }
                        if (includeSourceInShare) {
                            appendZenQuotesSource()
                        }
                    }
                    screenModel.shareQuote(shareText)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showShareSheet = false
                        }
                    }
                }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(bottom = 32.dp)
                            .verticalScroll(rememberScrollState())
                            .imePadding()
                            .navigationBarsPadding()
                ) {
                    Text(
                        "Share Quote",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quote Context
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = quote.q,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontStyle = FontStyle.Italic
                            )
                            Text(
                                text = "- ${quote.a}",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    SourceShareToggle(
                        includeSourceInShare = includeSourceInShare,
                        onIncludeSourceInShareChange = { includeSourceInShare = it },
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "My Take (optional)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        IconButton(
                            onClick = {
                                isUserTakeHidden = !isUserTakeHidden
                                isUserTakeFocused = false
                            }
                        ) {
                            Icon(
                                if (isUserTakeHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isUserTakeHidden) "Include My Take in share" else "Exclude My Take from share",
                                tint = if (isUserTakeHidden) Color.Gray else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        when {
                            isUserTakeHidden -> "Excluded from share"
                            isUserTakeActive -> "Included in share"
                            else -> "Leave blank to share just the quote."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (!isUserTakeHidden) {
                        OutlinedTextField(
                            value = userTake,
                            onValueChange = { newValue ->
                                userTake = newValue
                                if (newValue.isNotBlank()) {
                                    isUserTakeHidden = false
                                }
                            },
                            placeholder = { Text("Add your thoughts...", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    isUserTakeFocused = focusState.isFocused
                                },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                cursorColor = Color.White
                            ),
                            minLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { shareQuote() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Share Quote")
                    }
                }
            }
        }
    }
}
