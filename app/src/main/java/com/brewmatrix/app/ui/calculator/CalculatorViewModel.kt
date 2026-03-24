package com.brewmatrix.app.ui.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brewmatrix.app.data.local.entity.RatioPreset
import com.brewmatrix.app.data.repository.RatioPresetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val coffeeGrams: String = "18.0",
    val waterGrams: String = "288.0",
    val displayCoffee: Double = 18.0,
    val displayWater: Double = 288.0,
    val activePresetId: Long? = null,
    val presets: List<RatioPreset> = emptyList(),
    val activeRatio: Double = 16.0,
    val errorMessage: String? = null,
    val isEditingCoffee: Boolean = true,
    val showAddPresetDialog: Boolean = false,
    val showEditPresetDialog: Boolean = false,
    val showDeletePresetDialog: Boolean = false,
    val editingPreset: RatioPreset? = null,
)

class CalculatorViewModel(
    private val repository: RatioPresetRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    init {
        repository.getAllPresets()
            .onEach { presets ->
                _uiState.update { state ->
                    val updatedState = state.copy(presets = presets)
                    if (state.activePresetId == null && presets.isNotEmpty()) {
                        val firstPreset = presets.first()
                        updatedState.copy(
                            activePresetId = firstPreset.id,
                            activeRatio = firstPreset.ratio,
                        )
                    } else {
                        updatedState
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onCoffeeChanged(text: String) {
        val filtered = filterNumericInput(text)
        val coffeeValue = filtered.toDoubleOrNull()

        _uiState.update { state ->
            if (coffeeValue != null && coffeeValue in 0.1..9999.0) {
                val water = roundToOneDecimal(coffeeValue * state.activeRatio)
                state.copy(
                    coffeeGrams = filtered,
                    displayCoffee = coffeeValue,
                    waterGrams = formatGrams(water),
                    displayWater = water,
                    isEditingCoffee = true,
                    errorMessage = null,
                )
            } else if (filtered.isEmpty() || filtered == ".") {
                state.copy(
                    coffeeGrams = filtered,
                    isEditingCoffee = true,
                    errorMessage = null,
                )
            } else {
                state.copy(
                    coffeeGrams = filtered,
                    isEditingCoffee = true,
                    errorMessage = if (coffeeValue != null && coffeeValue > 9999.0)
                        "Max 9999g" else null,
                )
            }
        }
    }

    fun onWaterChanged(text: String) {
        val filtered = filterNumericInput(text)
        val waterValue = filtered.toDoubleOrNull()

        _uiState.update { state ->
            if (waterValue != null && waterValue in 0.1..9999.0) {
                val coffee = roundToOneDecimal(waterValue / state.activeRatio)
                state.copy(
                    waterGrams = filtered,
                    displayWater = waterValue,
                    coffeeGrams = formatGrams(coffee),
                    displayCoffee = coffee,
                    isEditingCoffee = false,
                    errorMessage = null,
                )
            } else if (filtered.isEmpty() || filtered == ".") {
                state.copy(
                    waterGrams = filtered,
                    isEditingCoffee = false,
                    errorMessage = null,
                )
            } else {
                state.copy(
                    waterGrams = filtered,
                    isEditingCoffee = false,
                    errorMessage = if (waterValue != null && waterValue > 9999.0)
                        "Max 9999g" else null,
                )
            }
        }
    }

    fun onPresetSelected(presetId: Long) {
        _uiState.update { state ->
            val preset = state.presets.find { it.id == presetId } ?: return@update state
            val water = roundToOneDecimal(state.displayCoffee * preset.ratio)
            state.copy(
                activePresetId = presetId,
                activeRatio = preset.ratio,
                waterGrams = formatGrams(water),
                displayWater = water,
            )
        }
    }

    fun onSavePreset(name: String, ratio: Double) {
        if (name.isBlank() || name.length > 100) return
        if (ratio !in 1.0..99.999) return
        viewModelScope.launch {
            val currentPresets = _uiState.value.presets
            val maxSortOrder = currentPresets.maxOfOrNull { it.sortOrder } ?: -1
            repository.insertPreset(
                RatioPreset(
                    name = name.trim(),
                    ratio = ratio,
                    isDefault = false,
                    sortOrder = maxSortOrder + 1,
                ),
            )
            _uiState.update { it.copy(showAddPresetDialog = false) }
        }
    }

    fun onEditPreset(presetId: Long, name: String, ratio: Double) {
        if (name.isBlank() || name.length > 100) return
        if (ratio !in 1.0..99.999) return
        viewModelScope.launch {
            val preset = _uiState.value.presets.find { it.id == presetId } ?: return@launch
            repository.updatePreset(preset.copy(name = name.trim(), ratio = ratio))
            _uiState.update { state ->
                if (state.activePresetId == presetId) {
                    val water = roundToOneDecimal(state.displayCoffee * ratio)
                    state.copy(
                        activeRatio = ratio,
                        waterGrams = formatGrams(water),
                        displayWater = water,
                        showEditPresetDialog = false,
                        editingPreset = null,
                    )
                } else {
                    state.copy(showEditPresetDialog = false, editingPreset = null)
                }
            }
        }
    }

    fun onDeletePreset(presetId: Long) {
        viewModelScope.launch {
            repository.deletePresetById(presetId)
            _uiState.update { state ->
                val newState = state.copy(
                    showDeletePresetDialog = false,
                    editingPreset = null,
                )
                if (state.activePresetId == presetId) {
                    val remaining = state.presets.filter { it.id != presetId }
                    if (remaining.isNotEmpty()) {
                        val first = remaining.first()
                        val water = roundToOneDecimal(state.displayCoffee * first.ratio)
                        newState.copy(
                            activePresetId = first.id,
                            activeRatio = first.ratio,
                            waterGrams = formatGrams(water),
                            displayWater = water,
                        )
                    } else {
                        newState.copy(activePresetId = null)
                    }
                } else {
                    newState
                }
            }
        }
    }

    fun onShowAddPresetDialog() {
        _uiState.update { it.copy(showAddPresetDialog = true) }
    }

    fun onDismissAddPresetDialog() {
        _uiState.update { it.copy(showAddPresetDialog = false) }
    }

    fun onShowEditPresetDialog(preset: RatioPreset) {
        _uiState.update { it.copy(showEditPresetDialog = true, editingPreset = preset) }
    }

    fun onDismissEditPresetDialog() {
        _uiState.update { it.copy(showEditPresetDialog = false, editingPreset = null) }
    }

    fun onShowDeletePresetDialog(preset: RatioPreset) {
        _uiState.update { it.copy(showDeletePresetDialog = true, editingPreset = preset) }
    }

    fun onDismissDeletePresetDialog() {
        _uiState.update { it.copy(showDeletePresetDialog = false, editingPreset = null) }
    }

    private fun filterNumericInput(text: String): String {
        val filtered = text.filter { it.isDigit() || it == '.' }
        val parts = filtered.split(".")
        return if (parts.size > 2) {
            parts[0] + "." + parts.drop(1).joinToString("")
        } else {
            filtered
        }
    }

    private fun roundToOneDecimal(value: Double): Double {
        return kotlin.math.round(value * 10.0) / 10.0
    }

    private fun formatGrams(value: Double): String {
        return String.format("%.1f", value)
    }

    class Factory(
        private val repository: RatioPresetRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
                return CalculatorViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
