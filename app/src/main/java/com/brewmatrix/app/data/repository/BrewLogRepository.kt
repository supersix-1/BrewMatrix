package com.brewmatrix.app.data.repository

import com.brewmatrix.app.data.local.dao.BrewLogDao
import com.brewmatrix.app.data.local.entity.BrewLog
import com.brewmatrix.app.data.model.BrewLogWithDetails
import kotlinx.coroutines.flow.Flow

class BrewLogRepository(private val brewLogDao: BrewLogDao) {

    fun getAllWithDetails(): Flow<List<BrewLogWithDetails>> =
        brewLogDao.getAllWithDetails()

    suspend fun insert(log: BrewLog): Long =
        brewLogDao.insert(log)

    suspend fun deleteById(id: Long) =
        brewLogDao.deleteById(id)
}
