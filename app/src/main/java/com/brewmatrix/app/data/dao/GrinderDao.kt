package com.brewmatrix.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brewmatrix.app.data.entity.Grinder
import kotlinx.coroutines.flow.Flow

@Dao
interface GrinderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(grinder: Grinder): Long

    @Update
    suspend fun update(grinder: Grinder)

    @Delete
    suspend fun delete(grinder: Grinder)

    @Query("SELECT * FROM grinders ORDER BY name ASC")
    fun getAll(): Flow<List<Grinder>>

    @Query("SELECT * FROM grinders WHERE id = :id")
    suspend fun getById(id: Long): Grinder?
}
