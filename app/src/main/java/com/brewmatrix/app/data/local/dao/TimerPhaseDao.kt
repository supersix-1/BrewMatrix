package com.brewmatrix.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brewmatrix.app.data.local.entity.TimerPhase

@Dao
interface TimerPhaseDao {

    @Query("SELECT * FROM timer_phase WHERE timer_preset_id = :presetId ORDER BY sort_order ASC")
    suspend fun getByPresetId(presetId: Long): List<TimerPhase>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(phases: List<TimerPhase>)

    @Query("DELETE FROM timer_phase WHERE timer_preset_id = :presetId")
    suspend fun deleteByPresetId(presetId: Long)
}
