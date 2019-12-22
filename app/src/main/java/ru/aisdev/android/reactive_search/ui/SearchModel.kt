package ru.aisdev.android.reactive_search.ui

import ru.aisdev.android.reactive_search.data.IWordsRepository
import ru.aisdev.android.reactive_search.entity.Word
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ru.aisdev.android.reactive_search.viewmodel.SearchViewModel
import ru.aisdev.android.reactive_search.viewmodel.SearchViewModelFactory
import java.util.concurrent.TimeUnit

class SearchModel(
    private var mainViewModel: SearchViewModel,
    private val repo: IWordsRepository,
    private val vmFactory: SearchViewModelFactory
) {

    private var filterSubject: PublishSubject<String> = PublishSubject.create()
    private val viewModelSubject: BehaviorSubject<SearchViewModel> = BehaviorSubject.create()

    private var loadWordsDisposable: Disposable? = null
    private var filterDisposable: Disposable? = null

    init {
        viewModelSubject.onNext(mainViewModel)
        loadWords()
        subscribeToFilter()
    }

    fun observeViewModels(): Observable<SearchViewModel> = viewModelSubject

    fun onFilterTextChanged(text: String) {
        filterSubject.onNext(text)
    }

    fun onDestroy() {
        loadWordsDisposable?.dispose()
        filterDisposable?.dispose()
    }

    private fun loadWords() {
        updateModel { copy(state = SearchViewModel.State.Loading) }
        loadWordsDisposable?.dispose()
        loadWordsDisposable = repo.loadWords()
            .subscribeOn(Schedulers.io())
            .subscribe(
                { items ->
                    updateModel(items)
                },
                {
                    updateModel { copy(state = SearchViewModel.State.Error(
                        "Check your internet connection"))}
                }
            )
    }

    private fun subscribeToFilter() {
        filterDisposable?.dispose()
        filterDisposable = filterSubject
            .debounce(200, TimeUnit.MILLISECONDS)
            .flatMapSingle { filter -> repo.loadWords()
                .map { words -> words.filter { it.word.startsWith(filter) }}
                .map { words -> words to filter } }
            .onErrorReturn { emptyList<Word>() to "" }
            .subscribeOn(Schedulers.io())
            .subscribe { (items, filter) ->
                updateModel(items, filter)
            }
    }

    private fun updateModel(items: List<Word>, filter: String = "") {
        if (items.isEmpty()) {
            updateModel {
                copy(
                    state = SearchViewModel.State.Empty,
                    filter = filter,
                    items = emptyList()
                )
            }
        } else {
            updateModel {
                copy(
                    state = SearchViewModel.State.Content,
                    filter = filter,
                    items = vmFactory.toUi(items, filter)
                )
            }
        }
    }

    private fun updateModel(mapper: SearchViewModel.() -> SearchViewModel = { this }) {
        mainViewModel = mainViewModel.mapper()
        viewModelSubject.onNext(mainViewModel)
    }
}