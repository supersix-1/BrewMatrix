package com.brewmatrix.app.ui.brewlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brewmatrix.app.data.local.entity.Bean
import com.brewmatrix.app.data.local.entity.BrewLog
import com.brewmatrix.app.data.local.entity.Grinder
import com.brewmatrix.app.data.model.BrewLogWithDetails
import com.brewmatrix.app.data.repository.BrewLogRepository
import com.brewmatrix.app.data.repository.GrindMemoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class BrewLogListItem {
    data class DateHeader(val label: String) : BrewLogListItem()
    data class LogEntry(val details: BrewLogWithDetails) : BrewLogListItem()
}

data class BrewLogUiState(
    val items: List<BrewLogListItem> = emptyList(),
    val isLoading: Boolean = true,
)

data class AddBrewFormState(
    val beans: List<Bean> = emptyList(),
    val grinders: List<Grinder> = emptyList(),
    val selectedBeanId: Long? = null,
    val selectedGrinderId: Long? = null,
    val ratioUsed: String = "15.0",
    val totalBrewTimeSeconds: String = "0",
    val rating: Int? = null,
    val note: String = "",
    val ratioError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
)

class BrewLogViewModel(
    private val brewLogRepository: BrewLogRepository,
    private val grindMemoryRepository: GrindMemoryRepository,
) : ViewModel() {

    val listState: StateFlow<BrewLogUiState> =
        brewLogRepository.getAllWithDetails()
            .map { logs ->
                val items = buildListItems(logs)
                BrewLogUiState(items = items, isLoading = false)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BrewLogUiState(),
            )

    private val _formState = MutableStateFlow(AddBrewFormState())
    val formState: StateFlow<AddBrewFormState> = _formState.asStateFlow()

    init {
        loadFormData()
    }

    private fun loadFormData() {
        viewModelScope.launch {
            grindMemoryRepository.getAllBeans().collect { beans ->
                _formState.update { it.copy(beans = beans) }
            }
        }
        viewModelScope.launch {
            grindMemoryRepository.getAllGrinders().collect { grinders ->
                _formState.update { it.copy(grinders = grinders) }
            }
        }
    }

    private fun buildListItems(logs: List<BrewLogWithDetails>): List<BrewLogListItem> {
        if (logs.isEmpty()) return emptyList()

        val today = startOfDay(System.currentTimeMillis())
        val yesterday = today - 86_400_000L
        val headerFmt = SimpleDateFormat("EEE, MMM d", Locale.getDefault())

        val result = mutableListOf<BrewLogListItem>()
        var lastDateKey = ""

        for (log in logs) {
            val dayStart = startOfDay(log.brewedAt)
            val dateKey = dayStart.toString()
            if (dateKey != lastDateKey) {
                val label = when {
                    dayStart >= today -> "TODAY"
                    dayStart >= yesterday -> "YESTERDAY"
                    else -> headerFmt.format(Date(log.brewedAt)).uppercase(Locale.getDefault())
                }
                result.add(BrewLogListItem.DateHeader(label))
                lastDateKey = dateKey
            }
            result.add(BrewLogListItem.LogEntry(log))
        }
        return result
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun deleteById(id: Long) {
        viewModelScope.launch {
            brewLogRepository.deleteById(id)
        }
    }

    fun onRatingChanged(rating: Int) {
        _formState.update { it.copy(rating = if (it.rating == rating) null else rating) }
    }

    fun onNoteChanged(note: String) {
        if (note.length <= 200) {
            _formState.update { it.copy(note = note) }
        }
    }

    fun onRatioChanged(ratio: String) {
        _formState.update { it.copy(ratioUsed = ratio, ratioError = null) }
    }

    fun onBrewTimeChanged(seconds: String) {
        _formState.update { it.copy(totalBrewTimeSeconds = seconds) }
    }

    fun onBeanSelected(id: Long?) {
        _formState.update { it.copy(selectedBeanId = id) }
    }

    fun onGrinderSelected(id: Long?) {
        _formState.update { it.copy(selectedGrinderId = id) }
    }

    fun saveBrewLog() {
        val form = _formState.value

        val ratio = form.ratioUsed.toDoubleOrNull()
        if (ratio == null || ratio < 1.0 || ratio > 99.999) {
            _formState.update { it.copy(ratioError = "Ratio must be between 1 and 99.999") }
            return
        }

        val brewSeconds = form.totalBrewTimeSeconds.toIntOrNull() ?: 0

        _formState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                brewLogRepository.insert(
                    BrewLog(
                        beanId = form.selectedBeanId,
                        grinderId = form.selectedGrinderId,
                        ratioUsed = ratio,
                        totalBrewTimeSeconds = brewSeconds,
                        rating = form.rating,
                        note = form.note.trim().ifBlank { null },
                    ),
                )
                _formState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _formState.update { it.copy(isSaving = false, ratioError = "Failed to save: ${e.message}") }
            }
        }
    }

    fun resetForm() {
        _formState.update {
            AddBrewFormState(beans = it.beans, grinders = it.grinders)
        }
    }

    class Factory(
        private val brewLogRepository: BrewLogRepository,
        private val grindMemoryRepository: GrindMemoryRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BrewLogViewModel(
                brewLogRepository = brewLogRepository,
                grindMemoryRepository = grindMemoryRepository,
            ) as T
        }
    }
}
