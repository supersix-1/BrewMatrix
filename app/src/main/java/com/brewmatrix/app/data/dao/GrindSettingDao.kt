package com.brewmatrix.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brewmatrix.app.data.entity.GrindSetting
import kotlinx.coroutines.flow.Flow

@Dao
interface GrindSettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(grindSetting: GrindSetting): Long

    @Delete
    suspend fun delete(grindSetting: GrindSetting)

    @Query("SELECT * FROM grind_settings WHERE grinder_id = :grinderId AND bean_id = :beanId")
    suspend fun getByGrinderAndBean(grinderId: Long, beanId: Long): GrindSetting?

    @Query("SELECT * FROM grind_settings ORDER BY last_used_at DESC")
    fun getAllSortedByLastUsed(): Flow<List<GrindSetting>>
}
