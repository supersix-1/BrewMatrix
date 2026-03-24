package com.brewmatrix.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.brewmatrix.app.data.local.entity.TimerPreset
import com.brewmatrix.app.data.model.TimerPresetWithPhases
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerPresetDao {

    @Query("SELECT * FROM timer_preset ORDER BY sort_order ASC")
    fun getAll(): Flow<List<TimerPreset>>

    @Query("SELECT * FROM timer_preset WHERE id = :id")
    suspend fun getById(id: Long): TimerPreset?

    @Transaction
    @Query("SELECT * FROM timer_preset WHERE id = :id")
    suspend fun getPresetWithPhases(id: Long): TimerPresetWithPhases?

    @Transaction
    @Query("SELECT * FROM timer_preset ORDER BY sort_order ASC")
    fun getAllWithPhases(): Flow<List<TimerPresetWithPhases>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: TimerPreset): Long

    @Update
    suspend fun update(preset: TimerPreset)

    @Query("DELETE FROM timer_preset WHERE id = :id")
    suspend fun deleteById(id: Long)
}
