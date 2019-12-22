package ru.aisdev.android.reactive_search

import android.app.Application
import ru.aisdev.android.reactive_search.di.MainComponent

class SearchApplicationModule : Application() {

    override fun onCreate() {
        super.onCreate()

        MAIN_COMPONENT =
            MainComponent(resources)
    }

    companion object {
        lateinit var MAIN_COMPONENT: MainComponent
    }
}