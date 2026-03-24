package com.brewmatrix.app.di

import android.content.Context
import com.brewmatrix.app.data.local.AppDatabase
import com.brewmatrix.app.data.repository.RatioPresetRepository

class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val ratioPresetRepository: RatioPresetRepository = RatioPresetRepository(
        ratioPresetDao = database.ratioPresetDao(),
    )
}
