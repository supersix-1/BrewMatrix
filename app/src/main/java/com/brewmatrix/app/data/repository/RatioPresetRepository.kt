package com.brewmatrix.app.data.repository

import com.brewmatrix.app.data.local.dao.RatioPresetDao
import com.brewmatrix.app.data.local.entity.RatioPreset
import kotlinx.coroutines.flow.Flow

class RatioPresetRepository(
    private val ratioPresetDao: RatioPresetDao,
) {

    fun getAllPresets(): Flow<List<RatioPreset>> = ratioPresetDao.getAll()

    suspend fun insertPreset(preset: RatioPreset): Long = ratioPresetDao.insert(preset)

    suspend fun updatePreset(preset: RatioPreset) = ratioPresetDao.update(preset)

    suspend fun deletePresetById(id: Long) = ratioPresetDao.deleteById(id)
}
