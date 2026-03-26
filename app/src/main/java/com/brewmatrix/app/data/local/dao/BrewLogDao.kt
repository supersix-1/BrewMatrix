package com.brewmatrix.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.brewmatrix.app.data.local.entity.BrewLog
import com.brewmatrix.app.data.model.BrewLogWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface BrewLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: BrewLog): Long

    @Query("DELETE FROM brew_log WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT
            bl.id AS id,
            bl.bean_id AS beanId,
            bl.grinder_id AS grinderId,
            bl.grind_setting_id AS grindSettingId,
            bl.ratio_used AS ratioUsed,
            bl.total_brew_time_seconds AS totalBrewTimeSeconds,
            bl.rating AS rating,
            bl.note AS note,
            bl.brewed_at AS brewedAt,
            b.name AS beanName,
            g.name AS grinderName
        FROM brew_log bl
        LEFT JOIN bean b ON bl.bean_id = b.id
        LEFT JOIN grinder g ON bl.grinder_id = g.id
        ORDER BY bl.brewed_at DESC
        """,
    )
    fun getAllWithDetails(): Flow<List<BrewLogWithDetails>>
}
