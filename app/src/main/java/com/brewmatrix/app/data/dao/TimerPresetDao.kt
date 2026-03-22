package com.brewmatrix.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brewmatrix.app.data.entity.TimerPreset
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerPresetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timerPreset: TimerPreset): Long

    @Update
    suspend fun update(timerPreset: TimerPreset)

    @Delete
    suspend fun delete(timerPreset: TimerPreset)

    @Query("SELECT * FROM timer_presets ORDER BY sort_order ASC")
    fun getAll(): Flow<List<TimerPreset>>

    @Query("SELECT * FROM timer_presets WHERE id = :id")
    suspend fun getById(id: Long): TimerPreset?
}
