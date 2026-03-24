package com.brewmatrix.app.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.brewmatrix.app.data.local.entity.TimerPhase
import com.brewmatrix.app.data.local.entity.TimerPreset

data class TimerPresetWithPhases(
    @Embedded
    val preset: TimerPreset,
    @Relation(
        parentColumn = "id",
        entityColumn = "timer_preset_id",
    )
    val phases: List<TimerPhase>,
)
