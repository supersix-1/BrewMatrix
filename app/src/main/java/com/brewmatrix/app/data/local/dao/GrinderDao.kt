package com.brewmatrix.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brewmatrix.app.data.local.entity.Grinder
import kotlinx.coroutines.flow.Flow

@Dao
interface GrinderDao {

    @Query("SELECT * FROM grinder ORDER BY name ASC")
    fun getAll(): Flow<List<Grinder>>

    @Query("SELECT * FROM grinder WHERE id = :id")
    suspend fun getById(id: Long): Grinder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(grinder: Grinder): Long

    @Update
    suspend fun update(grinder: Grinder)

    @Delete
    suspend fun delete(grinder: Grinder)

    @Query("DELETE FROM grinder WHERE id = :id")
    suspend fun deleteById(id: Long)
}
