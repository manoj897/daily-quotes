package com.dailyquotes.app.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dailyquotes.shared.Reflection
import com.dailyquotes.shared.ReflectionRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ReflectionsScreenModel(
    private val repository: ReflectionRepository
) : StateScreenModel<ReflectionsScreenModel.State>(State.Loading) {

    data class State(
        val reflections: List<Reflection> = emptyList(),
        val filteredReflections: List<Reflection> = emptyList(),
        val allTags: List<String> = emptyList(),
        val selectedTag: String? = null,
        val isLoading: Boolean = false
    )

    sealed class UIState {
        object Loading : State()
        // wait, I'm using StateScreenModel<State>
    }
    
    // Correcting pattern to use the data class State
    object Loading : State() // No, that won't work with the generic type easily if State is a data class

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
}
