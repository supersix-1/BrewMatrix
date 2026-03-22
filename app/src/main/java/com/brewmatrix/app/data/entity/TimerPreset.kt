package com.brewmatrix.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_presets")
data class TimerPreset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
)
