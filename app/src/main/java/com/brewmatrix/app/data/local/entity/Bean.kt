package com.brewmatrix.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bean")
data class Bean(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val roaster: String? = null,
    val origin: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)
