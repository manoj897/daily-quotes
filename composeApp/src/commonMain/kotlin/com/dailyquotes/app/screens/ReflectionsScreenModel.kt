package com.dailyquotes.app.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dailyquotes.shared.Reflection
import com.dailyquotes.shared.ReflectionRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReflectionsScreenModel(
    private val repository: ReflectionRepository
) : StateScreenModel<ReflectionsScreenModel.State>(State(isLoading = true)) {

    data class State(
        val reflections: List<Reflection> = emptyList(),
        val filteredReflections: List<Reflection> = emptyList(),
        val allTags: List<String> = emptyList(),
        val selectedTag: String? = null,
        val selectedIds: Set<Long> = emptySet(),
        val isLoading: Boolean = false
    )

    sealed class UIState {
        object Loading : UIState()
    }
    
    init {
        loadReflections()
    }

    fun loadReflections() {
        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            val reflections = repository.getAllReflections()
            val tags = repository.getAllTags()
            mutableState.update { 
                it.copy(
                    reflections = reflections,
                    filteredReflections = reflections,
                    allTags = tags,
                    isLoading = false
                ) 
            }
        }
    }

    fun filterByTag(tag: String?) {
        mutableState.update { state ->
            val filtered = if (tag == null) {
                state.reflections
            } else {
                state.reflections.filter { it.tags.contains(tag) }
            }
            state.copy(selectedTag = tag, filteredReflections = filtered)
        }
    }

    fun toggleSelection(id: Long) {
        mutableState.update { state ->
            val newSelection = if (state.selectedIds.contains(id)) {
                state.selectedIds - id
            } else {
                state.selectedIds + id
            }
            state.copy(selectedIds = newSelection)
        }
    }

    fun deleteSelected() {
        val idsToDelete = state.value.selectedIds
        if (idsToDelete.isEmpty()) return

        screenModelScope.launch {
            mutableState.update { it.copy(isLoading = true) }
            idsToDelete.forEach { id ->
                repository.deleteReflection(id)
            }
            loadReflections()
            mutableState.update { it.copy(selectedIds = emptySet()) }
        }
    }

    fun clearSelection() {
        mutableState.update { it.copy(selectedIds = emptySet()) }
    }
}
