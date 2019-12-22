package ru.aisdev.android.reactive_search.viewmodel

import ru.aisdev.android.reactive_search.entity.WordItem

data class SearchViewModel(
    val state: State,
    val filter: String,
    val items: List<WordItem>
) {

    sealed class State {
        object Loading : State()
        class Error(val text: String) : State()
        object Content : State()
        object Empty : State()
    }

}

