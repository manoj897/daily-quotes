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

class QuoteScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<QuoteScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

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
                        val quote = (state as QuoteScreenModel.State.Success).quote
                        IconButton(onClick = { screenModel.shareQuote(quote) }) {
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
                                text = "“",
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
    }
}
