package com.brewmatrix.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.brewmatrix.app.data.local.entity.Bean
import kotlinx.coroutines.flow.Flow

@Dao
interface BeanDao {

    @Query("SELECT * FROM bean ORDER BY name ASC")
    fun getAll(): Flow<List<Bean>>

    @Query("SELECT * FROM bean WHERE id = :id")
    suspend fun getById(id: Long): Bean?

    @Query("SELECT * FROM bean WHERE name LIKE '%' || :query || '%' OR roaster LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchByName(query: String): Flow<List<Bean>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bean: Bean): Long

    @Update
    suspend fun update(bean: Bean)

    @Delete
    suspend fun delete(bean: Bean)

    @Query("DELETE FROM bean WHERE id = :id")
    suspend fun deleteById(id: Long)
}
