package com.brewmatrix.app

import android.app.Application
import com.brewmatrix.app.di.AppContainer

class BrewMatrixApp : Application() {

    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}
