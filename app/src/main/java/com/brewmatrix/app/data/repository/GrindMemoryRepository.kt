package com.brewmatrix.app.data.repository

import com.brewmatrix.app.data.local.dao.BeanDao
import com.brewmatrix.app.data.local.dao.GrindSettingDao
import com.brewmatrix.app.data.local.dao.GrinderDao
import com.brewmatrix.app.data.local.entity.Bean
import com.brewmatrix.app.data.local.entity.Grinder
import com.brewmatrix.app.data.local.entity.GrindSetting
import com.brewmatrix.app.data.model.GrindSettingWithDetails
import kotlinx.coroutines.flow.Flow

class GrindMemoryRepository(
    private val grinderDao: GrinderDao,
    private val beanDao: BeanDao,
    private val grindSettingDao: GrindSettingDao,
) {

    // ── Grind Settings ──

    fun getAllSettingsWithDetails(): Flow<List<GrindSettingWithDetails>> =
        grindSettingDao.getAllWithDetailsSortedByLastUsed()

    suspend fun upsertSetting(grindSetting: GrindSetting): Long =
        grindSettingDao.upsert(grindSetting)

    suspend fun deleteSettingById(id: Long) =
        grindSettingDao.deleteById(id)

    suspend fun getSettingByGrinderAndBean(grinderId: Long, beanId: Long): GrindSetting? =
        grindSettingDao.getByGrinderAndBean(grinderId, beanId)

    suspend fun updateLastUsed(id: Long) =
        grindSettingDao.updateLastUsed(id)

    // ── Grinders ──

    fun getAllGrinders(): Flow<List<Grinder>> = grinderDao.getAll()

    suspend fun insertGrinder(grinder: Grinder): Long = grinderDao.insert(grinder)

    suspend fun updateGrinder(grinder: Grinder) = grinderDao.update(grinder)

    suspend fun deleteGrinderById(id: Long) = grinderDao.deleteById(id)

    // ── Beans ──

    fun getAllBeans(): Flow<List<Bean>> = beanDao.getAll()

    suspend fun insertBean(bean: Bean): Long = beanDao.insert(bean)

    suspend fun updateBean(bean: Bean) = beanDao.update(bean)

    suspend fun deleteBeanById(id: Long) = beanDao.deleteById(id)
}
