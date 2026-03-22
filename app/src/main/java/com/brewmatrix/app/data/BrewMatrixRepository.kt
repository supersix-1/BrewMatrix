package com.brewmatrix.app.data

import com.brewmatrix.app.data.dao.BeanDao
import com.brewmatrix.app.data.dao.BrewLogDao
import com.brewmatrix.app.data.dao.GrindSettingDao
import com.brewmatrix.app.data.dao.GrinderDao
import com.brewmatrix.app.data.dao.RatioPresetDao
import com.brewmatrix.app.data.dao.TimerPhaseDao
import com.brewmatrix.app.data.dao.TimerPresetDao
import com.brewmatrix.app.data.entity.Bean
import com.brewmatrix.app.data.entity.BrewLog
import com.brewmatrix.app.data.entity.GrindSetting
import com.brewmatrix.app.data.entity.Grinder
import com.brewmatrix.app.data.entity.RatioPreset
import com.brewmatrix.app.data.entity.TimerPhase
import com.brewmatrix.app.data.entity.TimerPreset
import kotlinx.coroutines.flow.Flow

class BrewMatrixRepository(
    private val grinderDao: GrinderDao,
    private val beanDao: BeanDao,
    private val grindSettingDao: GrindSettingDao,
    private val ratioPresetDao: RatioPresetDao,
    private val timerPresetDao: TimerPresetDao,
    private val timerPhaseDao: TimerPhaseDao,
    private val brewLogDao: BrewLogDao
) {
    suspend fun insertGrinder(grinder: Grinder): Long = grinderDao.insert(grinder)
    suspend fun updateGrinder(grinder: Grinder) = grinderDao.update(grinder)
    suspend fun deleteGrinder(grinder: Grinder) = grinderDao.delete(grinder)
    fun getAllGrinders(): Flow<List<Grinder>> = grinderDao.getAll()
    suspend fun getGrinderById(id: Long): Grinder? = grinderDao.getById(id)

suspend fun insertBean(bean: Bean): Long = beanDao.insert(bean)
    suspend fun updateBean(bean: Bean) = beanDao.update(bean)
    suspend fun deleteBean(bean: Bean) = beanDao.delete(bean)
    fun getAllBeans(): Flow<List<Bean>> = beanDao.getAll()
    suspend fun getBeanById(id: Long): Bean? = beanDao.getById(id)
    fun searchBeansByName(query: String): Flow<List<Bean>> = beanDao.searchByName(query)

suspend fun upsertGrindSetting(grindSetting: GrindSetting): Long = grindSettingDao.upsert(grindSetting)
    suspend fun deleteGrindSetting(grindSetting: GrindSetting) = grindSettingDao.delete(grindSetting)
    suspend fun getGrindSettingByGrinderAndBean(grinderId: Long, beanId: Long): GrindSetting? =
        grindSettingDao.getByGrinderAndBean(grinderId, beanId)
    fun getAllGrindSettingsSortedByLastUsed(): Flow<List<GrindSetting>> = grindSettingDao.getAllSortedByLastUsed()

suspend fun insertRatioPreset(ratioPreset: RatioPreset): Long = ratioPresetDao.insert(ratioPreset)
    suspend fun updateRatioPreset(ratioPreset: RatioPreset) = ratioPresetDao.update(ratioPreset)
    suspend fun deleteRatioPreset(ratioPreset: RatioPreset) = ratioPresetDao.delete(ratioPreset)
    fun getAllRatioPresets(): Flow<List<RatioPreset>> = ratioPresetDao.getAll()
    fun getDefaultRatioPresets(): Flow<List<RatioPreset>> = ratioPresetDao.getDefaults()

suspend fun insertTimerPreset(timerPreset: TimerPreset): Long = timerPresetDao.insert(timerPreset)
    suspend fun updateTimerPreset(timerPreset: TimerPreset) = timerPresetDao.update(timerPreset)
    suspend fun deleteTimerPreset(timerPreset: TimerPreset) = timerPresetDao.delete(timerPreset)
    fun getAllTimerPresets(): Flow<List<TimerPreset>> = timerPresetDao.getAll()
    suspend fun getTimerPresetById(id: Long): TimerPreset? = timerPresetDao.getById(id)

suspend fun insertTimerPhase(timerPhase: TimerPhase): Long = timerPhaseDao.insert(timerPhase)
    suspend fun updateTimerPhase(timerPhase: TimerPhase) = timerPhaseDao.update(timerPhase)
    suspend fun deleteTimerPhase(timerPhase: TimerPhase) = timerPhaseDao.delete(timerPhase)
    fun getTimerPhasesByPresetId(presetId: Long): Flow<List<TimerPhase>> = timerPhaseDao.getByPresetId(presetId)
    suspend fun getTimerPhasesByPresetIdOnce(presetId: Long): List<TimerPhase> = timerPhaseDao.getByPresetIdOnce(presetId)

suspend fun insertBrewLog(brewLog: BrewLog): Long = brewLogDao.insert(brewLog)
    suspend fun deleteBrewLog(brewLog: BrewLog) = brewLogDao.delete(brewLog)
    fun getAllBrewLogs(): Flow<List<BrewLog>> = brewLogDao.getAll()
}
