package ru.aisdev.android.reactive_search.di

import android.content.res.Resources
import ru.aisdev.android.reactive_search.ui.SearchModel
import ru.aisdev.android.reactive_search.data.WordsRepository
import ru.aisdev.android.reactive_search.viewmodel.SearchViewModel
import ru.aisdev.android.reactive_search.viewmodel.SearchViewModelFactory

class MainComponent(private val resources: Resources) {

    val searchModel: SearchModel by lazy {
        SearchModel(
            mainViewModel = SearchViewModel(
                state = SearchViewModel.State.Loading,
                filter = "",
                items = emptyList()
            ),
            repo = WordsRepository(
                resources
            ),
            vmFactory = SearchViewModelFactory()
        )
    }
}