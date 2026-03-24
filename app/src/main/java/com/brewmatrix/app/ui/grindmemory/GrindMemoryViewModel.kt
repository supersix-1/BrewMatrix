package com.brewmatrix.app.ui.grindmemory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brewmatrix.app.data.local.entity.Bean
import com.brewmatrix.app.data.local.entity.Grinder
import com.brewmatrix.app.data.local.entity.GrindSetting
import com.brewmatrix.app.data.local.entity.RatioPreset
import com.brewmatrix.app.data.model.GrindSettingWithDetails
import com.brewmatrix.app.data.repository.GrindMemoryRepository
import com.brewmatrix.app.data.repository.RatioPresetRepository
import com.brewmatrix.app.data.repository.TimerRepository
import com.brewmatrix.app.data.model.TimerPresetWithPhases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GrindMemoryUiState(
    val settings: List<GrindSettingWithDetails> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
)

data class AddEditFormState(
    // Grinder fields
    val grinders: List<Grinder> = emptyList(),
    val selectedGrinderId: Long? = null,
    val newGrinderName: String = "",
    val newGrinderType: String = "",
    val isCreatingNewGrinder: Boolean = false,
    // Bean fields
    val beans: List<Bean> = emptyList(),
    val selectedBeanId: Long? = null,
    val newBeanName: String = "",
    val newBeanRoaster: String = "",
    val newBeanOrigin: String = "",
    val isCreatingNewBean: Boolean = false,
    // Grind setting fields
    val settingText: String = "",
    val notes: String = "",
    // Linked presets
    val ratioPresets: List<RatioPreset> = emptyList(),
    val selectedRatioPresetId: Long? = null,
    val timerPresets: List<TimerPresetWithPhases> = emptyList(),
    val selectedTimerPresetId: Long? = null,
    // Validation
    val grinderError: String? = null,
    val beanError: String? = null,
    val settingError: String? = null,
    // State
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
)

class GrindMemoryViewModel(
    private val grindMemoryRepository: GrindMemoryRepository,
    private val ratioPresetRepository: RatioPresetRepository,
    private val timerRepository: TimerRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    val listState: StateFlow<GrindMemoryUiState> =
        combine(
            grindMemoryRepository.getAllSettingsWithDetails(),
            _searchQuery,
        ) { settings, query ->
            val filtered = if (query.isBlank()) {
                settings
            } else {
                settings.filter { item ->
                    item.beanName.contains(query, ignoreCase = true) ||
                        item.grinderName.contains(query, ignoreCase = true) ||
                        item.setting.contains(query, ignoreCase = true) ||
                        (item.beanRoaster?.contains(query, ignoreCase = true) == true)
                }
            }
            GrindMemoryUiState(
                settings = filtered,
                isLoading = false,
                searchQuery = query,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GrindMemoryUiState(),
        )

    private val _formState = MutableStateFlow(AddEditFormState())
    val formState: StateFlow<AddEditFormState> = _formState.asStateFlow()

    init {
        loadFormData()
    }

    private fun loadFormData() {
        viewModelScope.launch {
            grindMemoryRepository.getAllGrinders().collect { grinders ->
                _formState.update { it.copy(grinders = grinders) }
            }
        }
        viewModelScope.launch {
            grindMemoryRepository.getAllBeans().collect { beans ->
                _formState.update { it.copy(beans = beans) }
            }
        }
        viewModelScope.launch {
            ratioPresetRepository.getAllPresets().collect { presets ->
                _formState.update { it.copy(ratioPresets = presets) }
            }
        }
        viewModelScope.launch {
            timerRepository.getAllPresetsWithPhases().collect { presets ->
                _formState.update { it.copy(timerPresets = presets) }
            }
        }
    }

    // ── List screen actions ──

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun deleteSettingById(id: Long) {
        viewModelScope.launch {
            grindMemoryRepository.deleteSettingById(id)
        }
    }

    fun updateLastUsed(id: Long) {
        viewModelScope.launch {
            grindMemoryRepository.updateLastUsed(id)
        }
    }

    // ── Form actions ──

    fun selectGrinder(id: Long) {
        _formState.update {
            it.copy(selectedGrinderId = id, isCreatingNewGrinder = false, grinderError = null)
        }
    }

    fun toggleNewGrinder(creating: Boolean) {
        _formState.update {
            it.copy(
                isCreatingNewGrinder = creating,
                selectedGrinderId = if (creating) null else it.selectedGrinderId,
                grinderError = null,
            )
        }
    }

    fun updateNewGrinderName(name: String) {
        if (name.length <= 100) {
            _formState.update { it.copy(newGrinderName = name, grinderError = null) }
        }
    }

    fun updateNewGrinderType(type: String) {
        if (type.length <= 100) {
            _formState.update { it.copy(newGrinderType = type) }
        }
    }

    fun selectBean(id: Long) {
        _formState.update {
            it.copy(selectedBeanId = id, isCreatingNewBean = false, beanError = null)
        }
    }

    fun toggleNewBean(creating: Boolean) {
        _formState.update {
            it.copy(
                isCreatingNewBean = creating,
                selectedBeanId = if (creating) null else it.selectedBeanId,
                beanError = null,
            )
        }
    }

    fun updateNewBeanName(name: String) {
        if (name.length <= 100) {
            _formState.update { it.copy(newBeanName = name, beanError = null) }
        }
    }

    fun updateNewBeanRoaster(roaster: String) {
        if (roaster.length <= 100) {
            _formState.update { it.copy(newBeanRoaster = roaster) }
        }
    }

    fun updateNewBeanOrigin(origin: String) {
        if (origin.length <= 100) {
            _formState.update { it.copy(newBeanOrigin = origin) }
        }
    }

    fun updateSettingText(text: String) {
        if (text.length <= 50) {
            _formState.update { it.copy(settingText = text, settingError = null) }
        }
    }

    fun updateNotes(notes: String) {
        _formState.update { it.copy(notes = notes) }
    }

    fun selectRatioPreset(id: Long?) {
        _formState.update { it.copy(selectedRatioPresetId = id) }
    }

    fun selectTimerPreset(id: Long?) {
        _formState.update { it.copy(selectedTimerPresetId = id) }
    }

    fun saveGrindSetting() {
        val form = _formState.value

        // Validate
        val grinderError = when {
            form.isCreatingNewGrinder && form.newGrinderName.isBlank() -> "Grinder name cannot be blank"
            !form.isCreatingNewGrinder && form.selectedGrinderId == null -> "Please select a grinder"
            else -> null
        }
        val beanError = when {
            form.isCreatingNewBean && form.newBeanName.isBlank() -> "Bean name cannot be blank"
            !form.isCreatingNewBean && form.selectedBeanId == null -> "Please select a bean"
            else -> null
        }
        val settingError = when {
            form.settingText.isBlank() -> "Grind setting cannot be blank"
            else -> null
        }

        if (grinderError != null || beanError != null || settingError != null) {
            _formState.update {
                it.copy(
                    grinderError = grinderError,
                    beanError = beanError,
                    settingError = settingError,
                )
            }
            return
        }

        _formState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                // Create grinder if needed
                val grinderId = if (form.isCreatingNewGrinder) {
                    grindMemoryRepository.insertGrinder(
                        Grinder(
                            name = form.newGrinderName.trim(),
                            type = form.newGrinderType.trim().ifBlank { "Manual" },
                        )
                    )
                } else {
                    form.selectedGrinderId!!
                }

                // Create bean if needed
                val beanId = if (form.isCreatingNewBean) {
                    grindMemoryRepository.insertBean(
                        Bean(
                            name = form.newBeanName.trim(),
                            roaster = form.newBeanRoaster.trim().ifBlank { null },
                            origin = form.newBeanOrigin.trim().ifBlank { null },
                        )
                    )
                } else {
                    form.selectedBeanId!!
                }

                // Check for existing setting to get its ID (for upsert)
                val existing = grindMemoryRepository
                    .getSettingByGrinderAndBean(grinderId, beanId)

                val grindSetting = GrindSetting(
                    id = existing?.id ?: 0,
                    grinderId = grinderId,
                    beanId = beanId,
                    setting = form.settingText.trim(),
                    notes = form.notes.trim().ifBlank { null },
                    linkedRatioPresetId = form.selectedRatioPresetId,
                    linkedTimerPresetId = form.selectedTimerPresetId,
                    lastUsedAt = System.currentTimeMillis(),
                    createdAt = existing?.createdAt ?: System.currentTimeMillis(),
                )

                grindMemoryRepository.upsertSetting(grindSetting)
                _formState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _formState.update { it.copy(isSaving = false, settingError = "Failed to save: ${e.message}") }
            }
        }
    }

    fun resetForm() {
        _formState.update {
            AddEditFormState(
                grinders = it.grinders,
                beans = it.beans,
                ratioPresets = it.ratioPresets,
                timerPresets = it.timerPresets,
            )
        }
    }

    class Factory(
        private val grindMemoryRepository: GrindMemoryRepository,
        private val ratioPresetRepository: RatioPresetRepository,
        private val timerRepository: TimerRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GrindMemoryViewModel(
                grindMemoryRepository = grindMemoryRepository,
                ratioPresetRepository = ratioPresetRepository,
                timerRepository = timerRepository,
            ) as T
        }
    }
}
