package com.brewmatrix.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "brew_log",
    indices = [
        Index(value = ["bean_id"]),
        Index(value = ["grinder_id"]),
        Index(value = ["grind_setting_id"]),
    ],
    foreignKeys = [
        ForeignKey(
            entity = Bean::class,
            parentColumns = ["id"],
            childColumns = ["bean_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = Grinder::class,
            parentColumns = ["id"],
            childColumns = ["grinder_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = GrindSetting::class,
            parentColumns = ["id"],
            childColumns = ["grind_setting_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
)
data class BrewLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "bean_id")
    val beanId: Long? = null,
    @ColumnInfo(name = "grinder_id")
    val grinderId: Long? = null,
    @ColumnInfo(name = "grind_setting_id")
    val grindSettingId: Long? = null,
    @ColumnInfo(name = "ratio_used")
    val ratioUsed: Double,
    @ColumnInfo(name = "total_brew_time_seconds")
    val totalBrewTimeSeconds: Int,
    val rating: Int? = null,
    val note: String? = null,
    @ColumnInfo(name = "brewed_at")
    val brewedAt: Long = System.currentTimeMillis(),
)
