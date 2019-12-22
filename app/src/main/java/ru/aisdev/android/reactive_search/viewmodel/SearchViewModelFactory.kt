package ru.aisdev.android.reactive_search.viewmodel

import ru.aisdev.android.reactive_search.entity.Word
import ru.aisdev.android.reactive_search.entity.WordItem

class SearchViewModelFactory {

    fun toUi(words: List<Word>, selection: String): List<WordItem> =
        words.map {
            it.toUi(selection)
        }

    private fun Word.toUi(selection: String = "") =
        WordItem(
            text = this.word,
            selection = selection
        )
}