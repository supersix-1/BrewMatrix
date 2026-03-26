package com.brewmatrix.app.data.model

data class BrewLogWithDetails(
    val id: Long,
    val beanId: Long?,
    val grinderId: Long?,
    val grindSettingId: Long?,
    val ratioUsed: Double,
    val totalBrewTimeSeconds: Int,
    val rating: Int?,
    val note: String?,
    val brewedAt: Long,
    val beanName: String?,
    val grinderName: String?,
)
