package com.brewmatrix.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ratio_presets")
data class RatioPreset(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ratio: Double,
    @ColumnInfo(name = "is_default")
    val isDefault: Boolean = false,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0
)
