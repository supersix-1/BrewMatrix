package com.brewmatrix.app.di

import android.content.Context
import com.brewmatrix.app.data.local.AppDatabase
import com.brewmatrix.app.data.repository.RatioPresetRepository
import com.brewmatrix.app.data.repository.TimerRepository

class AppContainer(context: Context) {

    private val database = AppDatabase.getInstance(context)

    val ratioPresetRepository: RatioPresetRepository = RatioPresetRepository(
        ratioPresetDao = database.ratioPresetDao(),
    )

    val timerRepository: TimerRepository = TimerRepository(
        timerPresetDao = database.timerPresetDao(),
        timerPhaseDao = database.timerPhaseDao(),
    )
}
