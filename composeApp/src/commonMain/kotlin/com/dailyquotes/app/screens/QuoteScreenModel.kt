package com.dailyquotes.app.screens

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.dailyquotes.shared.Quote
import com.dailyquotes.shared.QuoteRepository
import com.dailyquotes.shared.ShareManager
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuoteScreenModel(
    private val repository: QuoteRepository,
    private val shareManager: ShareManager
) : StateScreenModel<QuoteScreenModel.State>(State.Loading) {

    sealed class State {
        object Loading : State()
        data class Success(val quote: Quote) : State()
        data class Error(val message: String) : State()
    }

    init {
        fetchQuote()
    }

    fun fetchQuote() {
        screenModelScope.launch {
            mutableState.update { State.Loading }
            val quote = repository.getDailyQuote()
            if (quote != null) {
                mutableState.update { State.Success(quote) }
            } else {
                mutableState.update { State.Error("Failed to fetch quote") }
            }
        }
    }

    fun shareQuote(quote: Quote) {
        shareManager.shareText("\"${quote.q}\" - ${quote.a}")
    }
}
