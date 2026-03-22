package com.brewmatrix.app

import android.app.Application

class BrewMatrixApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
