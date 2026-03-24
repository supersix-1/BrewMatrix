package com.brewmatrix.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "timer_phase",
    foreignKeys = [
        ForeignKey(
            entity = TimerPreset::class,
            parentColumns = ["id"],
            childColumns = ["timer_preset_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class TimerPhase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "timer_preset_id", index = true)
    val timerPresetId: Long,
    @ColumnInfo(name = "phase_name")
    val phaseName: String,
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,
    @ColumnInfo(name = "target_water_grams")
    val targetWaterGrams: Double? = null,
    @ColumnInfo(name = "sort_order")
    val sortOrder: Int,
)
