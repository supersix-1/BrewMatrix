package com.brewmatrix.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brewmatrix.app.data.entity.RatioPreset
import kotlinx.coroutines.flow.Flow

@Dao
interface RatioPresetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ratioPreset: RatioPreset): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(presets: List<RatioPreset>)

    @Update
    suspend fun update(ratioPreset: RatioPreset)

    @Delete
    suspend fun delete(ratioPreset: RatioPreset)

    @Query("SELECT * FROM ratio_presets ORDER BY sort_order ASC")
    fun getAll(): Flow<List<RatioPreset>>

    @Query("SELECT * FROM ratio_presets WHERE is_default = 1 ORDER BY sort_order ASC")
    fun getDefaults(): Flow<List<RatioPreset>>
}
