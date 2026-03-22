package com.brewmatrix.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brewmatrix.app.data.entity.Bean
import kotlinx.coroutines.flow.Flow

@Dao
interface BeanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: Bean): Long

    @Update
    suspend fun update(bean: Bean)

    @Delete
    suspend fun delete(bean: Bean)

    @Query("SELECT * FROM beans ORDER BY name ASC")
    fun getAll(): Flow<List<Bean>>

    @Query("SELECT * FROM beans WHERE id = :id")
    suspend fun getById(id: Long): Bean?

    @Query("SELECT * FROM beans WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchByName(query: String): Flow<List<Bean>>
}
