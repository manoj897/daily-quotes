package com.dailyquotes.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dailyquotes.shared.Quote

class ReflectionScreen(private val quote: Quote) : Screen {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ReflectionScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        if (state.saveSuccess) {
            LaunchedEffect(Unit) {
                navigator.pop()
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                    TextButton(
                        onClick = { screenModel.saveReflection(quote) },
                        enabled = !state.isSaving && state.note.isNotBlank()
                    ) {
                        Text("SAVE", color = if (state.note.isNotBlank()) Color.White else Color.Gray)
                    }
                }

                // Quote Preview (Abridged)
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF111111)),
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = quote.q,
                            color = Color(0xFFBBBBBB),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                        Text(
                            text = "- ${quote.a}",
                            color = Color(0xFF888888),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Note Field
                TextField(
                    value = state.note,
                    onValueChange = { screenModel.onNoteChange(it) },
                    placeholder = { 
                        Text(
                            "How does this apply to your life today?", 
                            color = Color.DarkGray,
                            fontSize = 18.sp
                        ) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, lineHeight = 28.sp)
                )

                // Tags Section
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    // Selected Tags
                    if (state.tags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            state.tags.forEach { tag ->
                                TagChip(tag = tag, onRemove = { screenModel.removeTag(tag) })
                            }
                        }
                    }

                    // Tag Input
                    OutlinedTextField(
                        value = state.tagInput,
                        onValueChange = { screenModel.onTagInputChange(it) },
                        placeholder = { Text("Add tag...") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        trailingIcon = {
                            if (state.tagInput.isNotBlank()) {
                                IconButton(onClick = { screenModel.addTag(state.tagInput) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Add")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.DarkGray
                        )
                    )

                    // Suggestions
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.suggestedTags) { tag ->
                            SuggestionChip(tag = tag, onClick = { screenModel.addTag(tag) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagChip(tag: String, onRemove: () -> Unit) {
    Surface(
        color = Color(0xFF222222),
        shape = CircleShape,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(tag, color = Color.White, fontSize = 12.sp)
            Spacer(Modifier.width(4.dp))
            Icon(
                Icons.Default.Close, 
                contentDescription = null, 
                modifier = Modifier.size(12.dp).clickable { onRemove() },
                tint = Color.Gray
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionChip(tag: String, onClick: () -> Unit) {
    Surface(
        color = Color.Transparent,
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.DarkGray),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            tag, 
            color = Color.LightGray, 
            fontSize = 12.sp, 
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}


