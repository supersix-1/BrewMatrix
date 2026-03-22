package com.brewmatrix.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timer_phases",
    foreignKeys = [
        ForeignKey(
            entity = TimerPreset::class,
            parentColumns = ["id"],
            childColumns = ["timer_preset_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["timer_preset_id"])]
)
data class TimerPhase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "timer_preset_id")
    val timerPresetId: Long,
    @ColumnInfo(name = "phase_order")
    val phaseOrder: Int,
    val name: String,
    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Int,
    @ColumnInfo(name = "target_water_grams")
    val targetWaterGrams: Double? = null
)
