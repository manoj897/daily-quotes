package com.dailyquotes.app.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dailyquotes.shared.Reflection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
class ReflectionsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ReflectionsScreenModel>()
        val state by screenModel.state.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        val isSelectionMode = state.selectedIds.isNotEmpty()
        
        var showShareSheet by remember { mutableStateOf(false) }
        var selectedReflectionForShare by remember { mutableStateOf<Reflection?>(null) }
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                if (isSelectionMode) {
                    TopAppBar(
                        title = { Text("${state.selectedIds.size} selected") },
                        navigationIcon = {
                            IconButton(onClick = { screenModel.clearSelection() }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear Selection")
                            }
                        },
                        actions = {
                            IconButton(onClick = { screenModel.deleteSelected() }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Selected", tint = Color.Red)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Black,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = { Text("REFLECTIONS", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) },
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Black,
                            titleContentColor = Color.White,
                            navigationIconContentColor = Color.White
                        )
                    )
                }
            },
            containerColor = Color.Black
        ) { padding ->
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                // Tag Filter Row (Only show when not in selection mode)
                if (!isSelectionMode && state.allTags.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = state.selectedTag == null,
                                onClick = { screenModel.filterByTag(null) },
                                label = { Text("All") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White,
                                    selectedLabelColor = Color.Black,
                                    labelColor = Color.White
                                )
                            )
                        }
                        items(state.allTags) { tag ->
                            FilterChip(
                                selected = state.selectedTag == tag,
                                onClick = { screenModel.filterByTag(tag) },
                                label = { Text(tag) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color.White,
                                    selectedLabelColor = Color.Black,
                                    labelColor = Color.White
                                )
                            )
                        }
                    }
                }

                if (state.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else if (state.filteredReflections.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No reflections yet.", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.filteredReflections) { reflection ->
                            val isSelected = state.selectedIds.contains(reflection.id)
                            ReflectionItem(
                                reflection = reflection,
                                isSelected = isSelected,
                                onLongClick = { screenModel.toggleSelection(reflection.id) },
                                onClick = { 
                                    if (isSelectionMode) {
                                        screenModel.toggleSelection(reflection.id)
                                    }
                                },
                                onShare = {
                                    selectedReflectionForShare = reflection
                                    showShareSheet = true
                                }
                            )
                        }
                    }
                }
            }
            
            if (showShareSheet && selectedReflectionForShare != null) {
                val reflection = selectedReflectionForShare!!
                ModalBottomSheet(
                    onDismissRequest = { showShareSheet = false },
                    sheetState = sheetState,
                    containerColor = Color(0xFF111111),
                    contentColor = Color.White
                ) {
                    var userTake by remember { mutableStateOf("") }
                    var includeReflection by remember { mutableStateOf(true) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            "Share Reflection",
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
                                    text = reflection.quoteContent,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                                Text(
                                    text = "- ${reflection.author}",
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Reflection section with delete option
                        if (reflection.note.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Reflection",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                                IconButton(onClick = { includeReflection = !includeReflection }) {
                                    Icon(
                                        if (includeReflection) Icons.Default.Delete else Icons.Default.Add,
                                        contentDescription = if (includeReflection) "Remove reflection" else "Add reflection",
                                        tint = if (includeReflection) Color.Red else Color.Green,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            if (includeReflection) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222)),
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                ) {
                                    Text(
                                        text = reflection.note,
                                        color = Color.LightGray,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                        }

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
                                    append("\"${reflection.quoteContent}\"")
                                    append("\n— ${reflection.author}")
                                    if (userTake.isNotBlank()) {
                                        append("\n\nMy Take\n${userTake}")
                                    }
                                    if (reflection.note.isNotEmpty() && includeReflection) {
                                        append("\n\nReflection\n${reflection.note}")
                                    }
                                }
                                screenModel.shareReflection(shareText)
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
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun ReflectionItem(
    reflection: Reflection, 
    isSelected: Boolean, 
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF333333) else Color(0xFF111111)
        ),
        border = if (isSelected) BorderStroke(1.dp, Color.White) else null,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = reflection.quoteContent,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "- ${reflection.author}",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            HorizontalDivider(color = Color(0xFF222222), modifier = Modifier.padding(vertical = 12.dp))
            
            Text(
                text = reflection.note,
                color = Color(0xFFBBBBBB),
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
            
            if (reflection.tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    reflection.tags.forEach { tag ->
                        Surface(
                            color = Color(0xFF222222),
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                tag, 
                                color = Color.LightGray, 
                                fontSize = 10.sp, 
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
        
        IconButton(
            onClick = onShare,
            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
        ) {
            Icon(
                Icons.Default.Share,
                contentDescription = "Share",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
  }
}


