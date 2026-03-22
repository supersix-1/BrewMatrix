package com.brewmatrix.app

import android.content.Context
import com.brewmatrix.app.data.BrewMatrixDatabase
import com.brewmatrix.app.data.BrewMatrixRepository

class AppContainer(context: Context) {
    val database = BrewMatrixDatabase.getInstance(context)
    val repository = BrewMatrixRepository(
        grinderDao = database.grinderDao(),
        beanDao = database.beanDao(),
        grindSettingDao = database.grindSettingDao(),
        ratioPresetDao = database.ratioPresetDao(),
        timerPresetDao = database.timerPresetDao(),
        timerPhaseDao = database.timerPhaseDao(),
        brewLogDao = database.brewLogDao()
    )
}
