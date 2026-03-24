package com.brewmatrix.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brewmatrix.app.data.local.entity.RatioPreset
import kotlinx.coroutines.flow.Flow

@Dao
interface RatioPresetDao {

    @Query("SELECT * FROM ratio_preset ORDER BY sort_order ASC")
    fun getAll(): Flow<List<RatioPreset>>

    @Query("SELECT * FROM ratio_preset WHERE is_default = 1 ORDER BY sort_order ASC")
    fun getDefaults(): Flow<List<RatioPreset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(preset: RatioPreset): Long

    @Update
    suspend fun update(preset: RatioPreset)

    @Delete
    suspend fun delete(preset: RatioPreset)

    @Query("DELETE FROM ratio_preset WHERE id = :id")
    suspend fun deleteById(id: Long)
}
