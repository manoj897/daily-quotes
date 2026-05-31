package com.dailyquotes.app.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dailyquotes.shared.Quote
import kotlinx.coroutines.delay

class ReflectionScreen(private val quote: Quote) : Screen {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ReflectionScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val tagScrollState = rememberScrollState()
        var isTagInputFocused by remember { mutableStateOf(false) }
        val density = LocalDensity.current
        val isImeVisible = WindowInsets.ime.getBottom(density) > 0
        val keyboardTopGuard = if (isImeVisible) 48.dp else 0.dp
        val selectedTagLimitText = when {
            state.tags.size >= ReflectionScreenModel.MAX_TAGS_PER_REFLECTION -> "${ReflectionScreenModel.MAX_TAGS_PER_REFLECTION} tag limit reached"
            state.tags.size >= ReflectionScreenModel.TAG_WARNING_THRESHOLD -> "${state.tags.size} of ${ReflectionScreenModel.MAX_TAGS_PER_REFLECTION} tags used"
            else -> null
        }
        val globalTagLimitText = when {
            state.globalTagCount >= ReflectionScreenModel.MAX_GLOBAL_TAGS -> "${ReflectionScreenModel.MAX_GLOBAL_TAGS} tag limit reached. Reuse an existing tag or remove old tags."
            state.globalTagCount >= ReflectionScreenModel.GLOBAL_TAG_WARNING_THRESHOLD -> "${state.globalTagCount} of ${ReflectionScreenModel.MAX_GLOBAL_TAGS} global tags used"
            else -> null
        }
        val canAddMoreTags = state.tags.size < ReflectionScreenModel.MAX_TAGS_PER_REFLECTION
        val tagSuggestionTitle = if (state.isShowingDefaultSuggestions) {
            "Frequent / recent"
        } else {
            "Matching tags"
        }
        val tagSuggestionMeta = if (
            state.isShowingDefaultSuggestions &&
            state.availableTagCount > state.suggestedTags.size
        ) {
            "${state.suggestedTags.size} of ${state.availableTagCount}"
        } else {
            null
        }

        if (state.saveSuccess) {
            LaunchedEffect(Unit) {
                navigator.pop()
            }
        }

        LaunchedEffect(isTagInputFocused, state.tags.size, tagScrollState.maxValue) {
            if (isTagInputFocused) {
                delay(100)
                tagScrollState.animateScrollTo(tagScrollState.maxValue)
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(top = keyboardTopGuard)
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                val sectionGap = (maxHeight * 0.018f).coerceIn(12.dp, 20.dp)
                val tagSectionMaxHeight = if (isImeVisible) {
                    (maxHeight * 0.42f).coerceIn(220.dp, 320.dp)
                } else {
                    (maxHeight * 0.34f).coerceIn(220.dp, 360.dp)
                }
                val noteMinHeight = if (isImeVisible) 96.dp else 140.dp

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isImeVisible) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF111111))
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
                        }

                        Spacer(modifier = Modifier.height(sectionGap))
                    }

                    // Note Field (outlined for visual emphasis)
                    OutlinedTextField(
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
                            .weight(1f)
                            .heightIn(min = noteMinHeight),
                        textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, lineHeight = 28.sp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.DarkGray,
                            cursorColor = Color.White
                        ),
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.height(sectionGap))

                    // Tags Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = tagSectionMaxHeight)
                            .verticalScroll(tagScrollState)
                            .padding(bottom = 4.dp)
                    ) {
                        // Suggestions stay visible above the input
                        if (state.suggestedTags.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tagSuggestionTitle,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                tagSuggestionMeta?.let {
                                    Text(
                                        text = it,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.suggestedTags.forEach { tag ->
                                    SuggestionChip(tag = tag, onClick = { screenModel.addTag(tag) })
                                }
                            }
                        }

                        // Selected Tags
                        if (state.tags.isNotEmpty()) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                state.tags.forEach { tag ->
                                    TagChip(tag = tag, onRemove = { screenModel.removeTag(tag) })
                                }
                            }
                        }

                        if (selectedTagLimitText != null || globalTagLimitText != null) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                selectedTagLimitText?.let {
                                    Text(
                                        text = it,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                                globalTagLimitText?.let {
                                    Text(
                                        text = it,
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        // Tag Input (kept in view when keyboard opens)
                        OutlinedTextField(
                            value = state.tagInput,
                            onValueChange = { screenModel.onTagInputChange(it) },
                            placeholder = { Text(if (canAddMoreTags) "Search / Add a tag" else "Tag limit reached") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .onFocusChanged { isTagInputFocused = it.isFocused },
                            enabled = canAddMoreTags,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            trailingIcon = {
                                if (state.tagInput.isNotBlank() && canAddMoreTags) {
                                    IconButton(
                                        onClick = { screenModel.addTag(state.tagInput) }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.DarkGray
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
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
