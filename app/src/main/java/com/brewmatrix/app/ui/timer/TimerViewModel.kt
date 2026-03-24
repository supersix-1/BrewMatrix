package com.brewmatrix.app.ui.timer

import android.app.Application
import android.os.PowerManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brewmatrix.app.data.local.entity.TimerPhase
import com.brewmatrix.app.data.model.TimerPresetWithPhases
import com.brewmatrix.app.data.repository.TimerRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TimerState { READY, RUNNING, PAUSED, COMPLETED }

sealed class TimerSideEffect {
    data object PhaseTransition : TimerSideEffect()
    data object Completed : TimerSideEffect()
}

data class TimerUiState(
    val timerState: TimerState = TimerState.READY,
    val presets: List<TimerPresetWithPhases> = emptyList(),
    val activePresetId: Long? = null,
    val phases: List<TimerPhase> = emptyList(),
    val currentPhaseIndex: Int = 0,
    val currentPhaseRemainingMillis: Long = 0L,
    val totalElapsedMillis: Long = 0L,
)

class TimerViewModel(
    private val repository: TimerRepository,
    private val application: Application,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private val _sideEffect = MutableSharedFlow<TimerSideEffect>(extraBufferCapacity = 5)
    val sideEffect: SharedFlow<TimerSideEffect> = _sideEffect.asSharedFlow()

    private var countdownJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    init {
        repository.getAllPresetsWithPhases()
            .onEach { presets ->
                _uiState.update { state ->
                    val updated = state.copy(presets = presets)
                    if (state.activePresetId == null && presets.isNotEmpty()) {
                        val first = presets.first()
                        val sortedPhases = first.phases.sortedBy { it.sortOrder }
                        updated.copy(
                            activePresetId = first.preset.id,
                            phases = sortedPhases,
                            currentPhaseRemainingMillis = sortedPhases.firstOrNull()
                                ?.let { it.durationSeconds * 1000L } ?: 0L,
                        )
                    } else {
                        updated
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onPresetSelected(presetId: Long) {
        val state = _uiState.value
        if (state.timerState == TimerState.RUNNING || state.timerState == TimerState.PAUSED) return

        val preset = state.presets.find { it.preset.id == presetId } ?: return
        val sortedPhases = preset.phases.sortedBy { it.sortOrder }

        _uiState.update {
            it.copy(
                activePresetId = presetId,
                phases = sortedPhases,
                currentPhaseIndex = 0,
                currentPhaseRemainingMillis = sortedPhases.firstOrNull()
                    ?.let { phase -> phase.durationSeconds * 1000L } ?: 0L,
                totalElapsedMillis = 0L,
                timerState = TimerState.READY,
            )
        }
    }

    fun onPlayPause() {
        when (_uiState.value.timerState) {
            TimerState.READY -> startTimer()
            TimerState.RUNNING -> pauseTimer()
            TimerState.PAUSED -> resumeTimer()
            TimerState.COMPLETED -> {} // No-op
        }
    }

    fun onReset() {
        countdownJob?.cancel()
        releaseWakeLock()

        val state = _uiState.value
        val sortedPhases = state.phases

        _uiState.update {
            it.copy(
                timerState = TimerState.READY,
                currentPhaseIndex = 0,
                currentPhaseRemainingMillis = sortedPhases.firstOrNull()
                    ?.let { phase -> phase.durationSeconds * 1000L } ?: 0L,
                totalElapsedMillis = 0L,
            )
        }
    }

    private fun startTimer() {
        acquireWakeLock()
        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startCountdown()
    }

    private fun pauseTimer() {
        countdownJob?.cancel()
        _uiState.update { it.copy(timerState = TimerState.PAUSED) }
    }

    private fun resumeTimer() {
        _uiState.update { it.copy(timerState = TimerState.RUNNING) }
        startCountdown()
    }

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            val tickInterval = 50L
            var lastTickTime = System.currentTimeMillis()

            while (true) {
                delay(tickInterval)
                val now = System.currentTimeMillis()
                val elapsed = now - lastTickTime
                lastTickTime = now

                val state = _uiState.value
                if (state.timerState != TimerState.RUNNING) break

                val newRemaining = state.currentPhaseRemainingMillis - elapsed
                val newTotalElapsed = state.totalElapsedMillis + elapsed

                if (newRemaining <= 0) {
                    // Phase complete
                    val nextIndex = state.currentPhaseIndex + 1
                    if (nextIndex < state.phases.size) {
                        // Advance to next phase
                        _sideEffect.emit(TimerSideEffect.PhaseTransition)
                        val nextPhase = state.phases[nextIndex]
                        _uiState.update {
                            it.copy(
                                currentPhaseIndex = nextIndex,
                                currentPhaseRemainingMillis = nextPhase.durationSeconds * 1000L + newRemaining,
                                totalElapsedMillis = newTotalElapsed,
                            )
                        }
                    } else {
                        // Timer complete
                        _sideEffect.emit(TimerSideEffect.Completed)
                        releaseWakeLock()
                        _uiState.update {
                            it.copy(
                                timerState = TimerState.COMPLETED,
                                currentPhaseRemainingMillis = 0L,
                                totalElapsedMillis = newTotalElapsed,
                            )
                        }
                        break
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            currentPhaseRemainingMillis = newRemaining,
                            totalElapsedMillis = newTotalElapsed,
                        )
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            val powerManager = application.getSystemService(PowerManager::class.java)
            wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
                "BrewMatrix::TimerWakeLock",
            )
        }
        wakeLock?.acquire(30 * 60 * 1000L) // 30 min max safety timeout
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        wakeLock = null
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
        releaseWakeLock()
    }

    class Factory(
        private val repository: TimerRepository,
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
                return TimerViewModel(repository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
