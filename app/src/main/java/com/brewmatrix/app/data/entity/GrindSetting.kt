package com.brewmatrix.app.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "grind_settings",
    foreignKeys = [
        ForeignKey(
            entity = Grinder::class,
            parentColumns = ["id"],
            childColumns = ["grinder_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Bean::class,
            parentColumns = ["id"],
            childColumns = ["bean_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RatioPreset::class,
            parentColumns = ["id"],
            childColumns = ["linked_ratio_preset_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = TimerPreset::class,
            parentColumns = ["id"],
            childColumns = ["linked_timer_preset_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["grinder_id", "bean_id"], unique = true),
        Index(value = ["grinder_id"]),
        Index(value = ["bean_id"]),
        Index(value = ["linked_ratio_preset_id"]),
        Index(value = ["linked_timer_preset_id"])
    ]
)
data class GrindSetting(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "grinder_id")
    val grinderId: Long,
    @ColumnInfo(name = "bean_id")
    val beanId: Long,
    val setting: String,
    val notes: String? = null,
    @ColumnInfo(name = "linked_ratio_preset_id")
    val linkedRatioPresetId: Long? = null,
    @ColumnInfo(name = "linked_timer_preset_id")
    val linkedTimerPresetId: Long? = null,
    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
