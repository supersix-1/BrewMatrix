package com.brewmatrix.app.data.model

/**
 * Flattened data class for displaying grind settings with joined details
 * from Grinder, Bean, and optionally linked presets. Used as the result
 * of the JOIN query in GrindSettingDao.
 */
data class GrindSettingWithDetails(
    val id: Long,
    val grinderId: Long,
    val beanId: Long,
    val setting: String,
    val notes: String?,
    val linkedRatioPresetId: Long?,
    val linkedTimerPresetId: Long?,
    val lastUsedAt: Long,
    val createdAt: Long,
    // Joined from Grinder
    val grinderName: String,
    val grinderType: String,
    // Joined from Bean
    val beanName: String,
    val beanRoaster: String?,
    // Joined from RatioPreset (nullable — may not be linked)
    val ratioPresetName: String?,
    val ratioValue: Double?,
    // Joined from TimerPreset (nullable — may not be linked)
    val timerPresetName: String?,
)
