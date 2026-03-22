package com.brewmatrix.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brewmatrix.app.data.entity.BrewLog
import kotlinx.coroutines.flow.Flow

@Dao
interface BrewLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(brewLog: BrewLog): Long

    @Delete
    suspend fun delete(brewLog: BrewLog)

    @Query("SELECT * FROM brew_logs ORDER BY brewed_at DESC")
    fun getAll(): Flow<List<BrewLog>>
}
