package com.brewmatrix.app.ui.settings

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.brewmatrix.app.data.local.entity.RatioPreset
import com.brewmatrix.app.data.model.BrewLogWithDetails
import com.brewmatrix.app.data.repository.BrewLogRepository
import com.brewmatrix.app.data.repository.PreferencesRepository
import com.brewmatrix.app.data.repository.RatioPresetRepository
import com.brewmatrix.app.di.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SettingsUiState(
    val themeMode: String = "system",
    val defaultRatioPresetId: Long? = null,
    val defaultDose: String = "18.0",
    val ratioPresets: List<RatioPreset> = emptyList(),
    val clearDataStep: ClearDataStep = ClearDataStep.NONE,
    val csvExportReady: Boolean = false,
    val csvExportIntent: Intent? = null,
    val appVersion: String = "1.0.0",
)

enum class ClearDataStep {
    NONE,
    FIRST_CONFIRM,
    FINAL_CONFIRM,
}

class SettingsViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val ratioPresetRepository: RatioPresetRepository,
    private val brewLogRepository: BrewLogRepository,
    private val appContainer: AppContainer,
    private val application: Application,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.themeMode.collect { mode ->
                _uiState.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.defaultRatioPresetId.collect { id ->
                _uiState.update { it.copy(defaultRatioPresetId = id) }
            }
        }
        viewModelScope.launch {
            preferencesRepository.defaultDose.collect { dose ->
                _uiState.update { it.copy(defaultDose = String.format("%.1f", dose)) }
            }
        }
        viewModelScope.launch {
            ratioPresetRepository.getAllPresets().collect { presets ->
                _uiState.update { it.copy(ratioPresets = presets) }
            }
        }
        // Read version from package info
        try {
            val versionName = application.packageManager
                .getPackageInfo(application.packageName, 0).versionName ?: "1.0.0"
            _uiState.update { it.copy(appVersion = versionName) }
        } catch (_: Exception) { /* keep default */ }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setDefaultRatioPreset(id: Long) {
        viewModelScope.launch {
            preferencesRepository.setDefaultRatioPresetId(id)
        }
    }

    fun setDefaultDose(doseText: String) {
        val dose = doseText.toDoubleOrNull() ?: return
        if (dose < 0.1 || dose > 9999.0) return
        _uiState.update { it.copy(defaultDose = doseText) }
        viewModelScope.launch {
            preferencesRepository.setDefaultDose(dose)
        }
    }

    fun requestClearData() {
        _uiState.update { it.copy(clearDataStep = ClearDataStep.FIRST_CONFIRM) }
    }

    fun confirmClearDataStep1() {
        _uiState.update { it.copy(clearDataStep = ClearDataStep.FINAL_CONFIRM) }
    }

    fun confirmClearDataFinal() {
        viewModelScope.launch {
            appContainer.clearAllData()
            _uiState.update { it.copy(clearDataStep = ClearDataStep.NONE) }
        }
    }

    fun dismissClearData() {
        _uiState.update { it.copy(clearDataStep = ClearDataStep.NONE) }
    }

    fun exportBrewLogCsv(context: Context) {
        viewModelScope.launch {
            val logs = brewLogRepository.getAllWithDetails().first()
            if (logs.isEmpty()) {
                _uiState.update { it.copy(csvExportReady = false, csvExportIntent = null) }
                return@launch
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val fileDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

            val csv = buildString {
                appendLine("Date,Bean,Grinder,Ratio,Brew Time (s),Rating,Notes")
                logs.forEach { log ->
                    appendLine(formatCsvRow(log, dateFormat))
                }
            }

            val exportDir = File(context.cacheDir, "exports")
            exportDir.mkdirs()
            val file = File(exportDir, "brewmatrix_export_$fileDate.csv")
            file.writeText(csv)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            _uiState.update {
                it.copy(
                    csvExportReady = true,
                    csvExportIntent = Intent.createChooser(shareIntent, "Export Brew Log"),
                )
            }
        }
    }

    fun clearExportState() {
        _uiState.update { it.copy(csvExportReady = false, csvExportIntent = null) }
    }

    private fun formatCsvRow(log: BrewLogWithDetails, dateFormat: SimpleDateFormat): String {
        val date = dateFormat.format(Date(log.brewedAt))
        val bean = escapeCsv(log.beanName ?: "")
        val grinder = escapeCsv(log.grinderName ?: "")
        val ratio = "1:${String.format("%.3f", log.ratioUsed)}"
        val time = log.totalBrewTimeSeconds.toString()
        val rating = log.rating?.toString() ?: ""
        val notes = escapeCsv(log.note ?: "")
        return "$date,$bean,$grinder,$ratio,$time,$rating,$notes"
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"${value.replace("\"", "\"\"")}\""
        } else {
            value
        }
    }

    class Factory(
        private val preferencesRepository: PreferencesRepository,
        private val ratioPresetRepository: RatioPresetRepository,
        private val brewLogRepository: BrewLogRepository,
        private val appContainer: AppContainer,
        private val application: Application,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
                return SettingsViewModel(
                    preferencesRepository = preferencesRepository,
                    ratioPresetRepository = ratioPresetRepository,
                    brewLogRepository = brewLogRepository,
                    appContainer = appContainer,
                    application = application,
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
