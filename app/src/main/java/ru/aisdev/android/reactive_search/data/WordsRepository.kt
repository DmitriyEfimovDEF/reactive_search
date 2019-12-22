package ru.aisdev.android.reactive_search.data

import android.content.res.Resources
import com.example.android.reactive_search.R
import ru.aisdev.android.reactive_search.entity.Word
import io.reactivex.Single

interface IWordsRepository {
    fun loadWords(): Single<List<Word>>
}

class WordsRepository(private val resources: Resources) : IWordsRepository {

    override fun loadWords(): Single<List<Word>> =
        Single
            .fromCallable {
                resources
                    .getStringArray( R.array.words)
                    .map { Word(it) }
            }
            .cache()
}