package com.dailyquotes.app.screens

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dailyquotes.shared.Quote
import com.dailyquotes.shared.ReflectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReflectionScreenModel(
    private val repository: ReflectionRepository
) : ScreenModel {

    data class State(
        val note: String = "",
        val tags: List<String> = emptyList(),
        val tagInput: String = "",
        val suggestedTags: List<String> = emptyList(),
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val allHistoryTags = mutableListOf<String>()

    init {
        loadTags()
    }

    private fun loadTags() {
        screenModelScope.launch {
            val dbTags = repository.getAllTags()
            allHistoryTags.clear()
            allHistoryTags.addAll(dbTags)
            // Pre-seed if empty as per PRD
            if (allHistoryTags.isEmpty()) {
                allHistoryTags.addAll(listOf("Work", "Family", "Meetings", "Workouts", "Relationships", "Goals", "Gratitude", "Health"))
            }
            updateSuggestions("")
        }
    }

    fun onNoteChange(newNote: String) {
        _state.update { it.copy(note = newNote) }
    }

    fun onTagInputChange(newInput: String) {
        _state.update { it.copy(tagInput = newInput) }
        updateSuggestions(newInput)
    }

    private fun updateSuggestions(input: String) {
        val suggested = if (input.isEmpty()) {
            allHistoryTags.filter { !state.value.tags.contains(it) }.take(8)
        } else {
            allHistoryTags.filter { it.contains(input, ignoreCase = true) && !state.value.tags.contains(it) }
        }
        _state.update { it.copy(suggestedTags = suggested) }
    }

    fun addTag(tag: String) {
        val trimmed = tag.trim()
        if (trimmed.isNotEmpty() && !state.value.tags.contains(trimmed)) {
            _state.update { it.copy(
                tags = it.tags + trimmed,
                tagInput = ""
            ) }
            updateSuggestions("")
        }
    }

    fun removeTag(tag: String) {
        _state.update { it.copy(tags = it.tags - tag) }
        updateSuggestions(state.value.tagInput)
    }

    fun saveReflection(quote: Quote) {
        screenModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            repository.saveReflection(quote, state.value.note, state.value.tags)
            _state.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }
}
