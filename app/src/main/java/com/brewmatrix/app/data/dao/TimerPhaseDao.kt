package com.brewmatrix.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brewmatrix.app.data.entity.TimerPhase
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerPhaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timerPhase: TimerPhase): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phases: List<TimerPhase>)

    @Update
    suspend fun update(timerPhase: TimerPhase)

    @Delete
    suspend fun delete(timerPhase: TimerPhase)

    @Query("SELECT * FROM timer_phases WHERE timer_preset_id = :presetId ORDER BY phase_order ASC")
    fun getByPresetId(presetId: Long): Flow<List<TimerPhase>>

    @Query("SELECT * FROM timer_phases WHERE timer_preset_id = :presetId ORDER BY phase_order ASC")
    suspend fun getByPresetIdOnce(presetId: Long): List<TimerPhase>
}
