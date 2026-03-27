package com.brewmatrix.app.di

import android.content.Context
import com.brewmatrix.app.data.local.AppDatabase
import com.brewmatrix.app.data.repository.BrewLogRepository
import com.brewmatrix.app.data.repository.GrindMemoryRepository
import com.brewmatrix.app.data.repository.PreferencesRepository
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

    val grindMemoryRepository: GrindMemoryRepository = GrindMemoryRepository(
        grinderDao = database.grinderDao(),
        beanDao = database.beanDao(),
        grindSettingDao = database.grindSettingDao(),
    )

    val brewLogRepository: BrewLogRepository = BrewLogRepository(
        brewLogDao = database.brewLogDao(),
    )

    val preferencesRepository: PreferencesRepository = PreferencesRepository(context)

    suspend fun clearAllData() {
        database.clearAllTables()
        AppDatabase.reseedDefaults(database)
        preferencesRepository.clearAll()
    }
}
