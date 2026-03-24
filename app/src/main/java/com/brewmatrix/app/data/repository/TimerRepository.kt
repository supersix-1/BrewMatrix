package com.brewmatrix.app.data.repository

import com.brewmatrix.app.data.local.dao.TimerPhaseDao
import com.brewmatrix.app.data.local.dao.TimerPresetDao
import com.brewmatrix.app.data.local.entity.TimerPhase
import com.brewmatrix.app.data.local.entity.TimerPreset
import com.brewmatrix.app.data.model.TimerPresetWithPhases
import kotlinx.coroutines.flow.Flow

class TimerRepository(
    private val timerPresetDao: TimerPresetDao,
    private val timerPhaseDao: TimerPhaseDao,
) {

    fun getAllPresetsWithPhases(): Flow<List<TimerPresetWithPhases>> =
        timerPresetDao.getAllWithPhases()

    suspend fun getPresetWithPhases(id: Long): TimerPresetWithPhases? =
        timerPresetDao.getPresetWithPhases(id)

    suspend fun insertPresetWithPhases(preset: TimerPreset, phases: List<TimerPhase>): Long {
        val presetId = timerPresetDao.insert(preset)
        val phasesWithPresetId = phases.map { it.copy(timerPresetId = presetId) }
        timerPhaseDao.insertAll(phasesWithPresetId)
        return presetId
    }

    suspend fun deletePreset(id: Long) {
        timerPhaseDao.deleteByPresetId(id)
        timerPresetDao.deleteById(id)
    }
}
