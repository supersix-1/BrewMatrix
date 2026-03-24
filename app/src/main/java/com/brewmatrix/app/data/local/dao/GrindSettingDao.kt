package com.brewmatrix.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brewmatrix.app.data.local.entity.GrindSetting
import com.brewmatrix.app.data.model.GrindSettingWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface GrindSettingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(grindSetting: GrindSetting): Long

    @Query(
        """
        SELECT 
            gs.id AS id,
            gs.grinder_id AS grinderId,
            gs.bean_id AS beanId,
            gs.setting AS setting,
            gs.notes AS notes,
            gs.linked_ratio_preset_id AS linkedRatioPresetId,
            gs.linked_timer_preset_id AS linkedTimerPresetId,
            gs.last_used_at AS lastUsedAt,
            gs.created_at AS createdAt,
            g.name AS grinderName,
            g.type AS grinderType,
            b.name AS beanName,
            b.roaster AS beanRoaster,
            rp.name AS ratioPresetName,
            rp.ratio AS ratioValue,
            tp.name AS timerPresetName
        FROM grind_setting gs
        INNER JOIN grinder g ON gs.grinder_id = g.id
        INNER JOIN bean b ON gs.bean_id = b.id
        LEFT JOIN ratio_preset rp ON gs.linked_ratio_preset_id = rp.id
        LEFT JOIN timer_preset tp ON gs.linked_timer_preset_id = tp.id
        ORDER BY gs.last_used_at DESC
        """
    )
    fun getAllWithDetailsSortedByLastUsed(): Flow<List<GrindSettingWithDetails>>

    @Query("SELECT * FROM grind_setting WHERE grinder_id = :grinderId AND bean_id = :beanId")
    suspend fun getByGrinderAndBean(grinderId: Long, beanId: Long): GrindSetting?

    @Query("DELETE FROM grind_setting WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE grind_setting SET last_used_at = :timestamp WHERE id = :id")
    suspend fun updateLastUsed(id: Long, timestamp: Long = System.currentTimeMillis())
}
