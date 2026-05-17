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
    companion object {
        const val MAX_TAGS_PER_REFLECTION = 20
        const val TAG_WARNING_THRESHOLD = 15
        const val MAX_GLOBAL_TAGS = 100
        const val GLOBAL_TAG_WARNING_THRESHOLD = 90
        const val DEFAULT_SUGGESTION_LIMIT = 12
        const val SEARCH_SUGGESTION_LIMIT = 12
    }

    data class State(
        val note: String = "",
        val tags: List<String> = emptyList(),
        val tagInput: String = "",
        val suggestedTags: List<String> = emptyList(),
        val globalTagCount: Int = 0,
        val availableTagCount: Int = 0,
        val isShowingDefaultSuggestions: Boolean = true,
        val isSaving: Boolean = false,
        val saveSuccess: Boolean = false
    )

    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private val allHistoryTags = mutableListOf<String>()
    private val persistedTagNames = mutableListOf<String>()

    init {
        loadTags()
    }

    private fun loadTags() {
        screenModelScope.launch {
            val dbTags = repository.getAllTags()
            persistedTagNames.clear()
            persistedTagNames.addAll(dbTags)
            allHistoryTags.clear()
            allHistoryTags.addAll(dbTags)
            // Pre-seed if empty as per PRD
            if (allHistoryTags.isEmpty()) {
                allHistoryTags.addAll(listOf("Work", "Family", "Meetings", "Workouts", "Relationships", "Goals", "Gratitude", "Health"))
            }
            _state.update { it.copy(
                globalTagCount = dbTags.size,
                availableTagCount = allHistoryTags.size
            ) }
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
        val selectedTags = state.value.tags
        val normalizedInput = input.trim()
        if (selectedTags.size >= MAX_TAGS_PER_REFLECTION) {
            _state.update { it.copy(
                suggestedTags = emptyList(),
                isShowingDefaultSuggestions = normalizedInput.isEmpty()
            ) }
            return
        }

        val isDefaultSuggestions = normalizedInput.isEmpty()
        val candidates = if (isDefaultSuggestions) {
            allHistoryTags.filter { !selectedTags.contains(it) }
        } else {
            allHistoryTags.filter { it.contains(normalizedInput, ignoreCase = true) && !selectedTags.contains(it) }
        }
        val limit = if (isDefaultSuggestions) DEFAULT_SUGGESTION_LIMIT else SEARCH_SUGGESTION_LIMIT
        _state.update { it.copy(
            suggestedTags = candidates.take(limit),
            availableTagCount = allHistoryTags.size,
            isShowingDefaultSuggestions = isDefaultSuggestions
        ) }
    }

    fun addTag(tag: String) {
        val trimmed = tag.trim()
        val currentState = state.value
        val currentTags = currentState.tags

        if (trimmed.isNotEmpty() && !currentTags.contains(trimmed)) {
            if (currentTags.size >= MAX_TAGS_PER_REFLECTION) {
                updateSuggestions(currentState.tagInput)
                return
            }

            val updatedTags = currentTags + trimmed
            val projectedGlobalTagCount = effectiveGlobalTagCount(updatedTags)
            if (projectedGlobalTagCount > MAX_GLOBAL_TAGS) {
                updateSuggestions(currentState.tagInput)
                return
            }

            _state.update { it.copy(
                tags = updatedTags,
                tagInput = "",
                globalTagCount = projectedGlobalTagCount
            ) }
            updateSuggestions("")
        }
    }

    fun removeTag(tag: String) {
        val updatedTags = state.value.tags - tag
        _state.update { it.copy(
            tags = updatedTags,
            globalTagCount = effectiveGlobalTagCount(updatedTags)
        ) }
        updateSuggestions(state.value.tagInput)
    }

    fun saveReflection(quote: Quote) {
        screenModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            repository.saveReflection(quote, state.value.note, state.value.tags)
            _state.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    private fun effectiveGlobalTagCount(selectedTags: List<String>): Int {
        val pendingNewTags = selectedTags
            .filterNot { persistedTagNames.contains(it) }
            .distinct()
        return persistedTagNames.size + pendingNewTags.size
    }
}
