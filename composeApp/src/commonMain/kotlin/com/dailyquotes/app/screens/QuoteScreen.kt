package com.dailyquotes.app.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class QuoteScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<QuoteScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        
        var showShareSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()

        Scaffold(
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    IconButton(onClick = { navigator.push(ReflectionsScreen()) }) {
                        Icon(Icons.Default.List, contentDescription = "Reflections")
                    }
                    Spacer(Modifier.weight(1f))
                    if (state is QuoteScreenModel.State.Success) {
                        IconButton(onClick = { showShareSheet = true }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
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
                        Text(currentState.message, color = MaterialTheme.colorScheme.error)
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
                            
                            Spacer(Modifier.height(48.dp))
                            
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

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(bottom = 32.dp)
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

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "My Take",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = userTake,
                        onValueChange = { userTake = it },
                        placeholder = { Text("Add your thoughts...", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val shareText = buildString {
                                append("\"${quote.q}\"")
                                append("\n— ${quote.a}")
                                if (userTake.isNotBlank()) {
                                    append("\n\nMy Take\n$userTake")
                                }
                            }
                            screenModel.shareQuote(shareText)
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showShareSheet = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text("Share Now")
                    }
                }
            }
        }
    }
}
