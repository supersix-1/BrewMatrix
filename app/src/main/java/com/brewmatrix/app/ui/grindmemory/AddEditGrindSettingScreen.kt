package com.brewmatrix.app.ui.grindmemory

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewmatrix.app.ui.theme.BrewMatrixTheme
import com.brewmatrix.app.ui.theme.DmSans

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditGrindSettingScreen(
    viewModel: GrindMemoryViewModel,
    onNavigateBack: () -> Unit,
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val extraColors = BrewMatrixTheme.extraColors

    LaunchedEffect(formState.saveSuccess) {
        if (formState.saveSuccess) {
            onNavigateBack()
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
        unfocusedBorderColor = extraColors.subtleBorder,
        cursorColor = MaterialTheme.colorScheme.tertiary,
        focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        unfocusedLabelColor = extraColors.secondaryText,
        errorBorderColor = MaterialTheme.colorScheme.error,
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Grind Setting",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Section: Grinder ──
            SectionHeader("Grinder")
            Spacer(modifier = Modifier.height(8.dp))

            if (formState.isCreatingNewGrinder) {
                // New grinder form
                OutlinedTextField(
                    value = formState.newGrinderName,
                    onValueChange = viewModel::updateNewGrinderName,
                    label = { Text("Grinder name") },
                    isError = formState.grinderError != null,
                    supportingText = formState.grinderError?.let { { Text(it) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = formState.newGrinderType,
                    onValueChange = viewModel::updateNewGrinderType,
                    label = { Text("Type (e.g. Hand Burr, Electric Flat)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "← Pick existing grinder",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable { viewModel.toggleNewGrinder(false) },
                )
            } else {
                // Grinder picker chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    formState.grinders.forEach { grinder ->
                        SelectableChip(
                            label = grinder.name,
                            isSelected = formState.selectedGrinderId == grinder.id,
                            onClick = { viewModel.selectGrinder(grinder.id) },
                        )
                    }
                    // "+ New" chip
                    SelectableChip(
                        label = "+ New",
                        isSelected = false,
                        isAccent = true,
                        onClick = { viewModel.toggleNewGrinder(true) },
                    )
                }
                if (formState.grinderError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formState.grinderError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Section: Bean ──
            SectionHeader("Bean")
            Spacer(modifier = Modifier.height(8.dp))

            if (formState.isCreatingNewBean) {
                OutlinedTextField(
                    value = formState.newBeanName,
                    onValueChange = viewModel::updateNewBeanName,
                    label = { Text("Bean name") },
                    isError = formState.beanError != null,
                    supportingText = formState.beanError?.let { { Text(it) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = formState.newBeanRoaster,
                        onValueChange = viewModel::updateNewBeanRoaster,
                        label = { Text("Roaster") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.weight(1f),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = formState.newBeanOrigin,
                        onValueChange = viewModel::updateNewBeanOrigin,
                        label = { Text("Origin") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors,
                        modifier = Modifier.weight(1f),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "← Pick existing bean",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.clickable { viewModel.toggleNewBean(false) },
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    formState.beans.forEach { bean ->
                        val label = if (bean.roaster != null) {
                            "${bean.name} (${bean.roaster})"
                        } else {
                            bean.name
                        }
                        SelectableChip(
                            label = label,
                            isSelected = formState.selectedBeanId == bean.id,
                            onClick = { viewModel.selectBean(bean.id) },
                        )
                    }
                    SelectableChip(
                        label = "+ New",
                        isSelected = false,
                        isAccent = true,
                        onClick = { viewModel.toggleNewBean(true) },
                    )
                }
                if (formState.beanError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formState.beanError!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Section: Grind Setting ──
            SectionHeader("Grind Setting")
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = formState.settingText,
                onValueChange = viewModel::updateSettingText,
                label = { Text("Setting (e.g. 14 clicks, 3.5)") },
                isError = formState.settingError != null,
                supportingText = formState.settingError?.let { { Text(it) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = formState.notes,
                onValueChange = viewModel::updateNotes,
                label = { Text("Notes (optional)") },
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Section: Link Presets (Optional) ──
            SectionHeader("Link Presets (optional)")
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ratio Preset",
                style = MaterialTheme.typography.labelMedium,
                color = extraColors.secondaryText,
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SelectableChip(
                    label = "None",
                    isSelected = formState.selectedRatioPresetId == null,
                    onClick = { viewModel.selectRatioPreset(null) },
                )
                formState.ratioPresets.forEach { preset ->
                    SelectableChip(
                        label = "${preset.name} (1:${preset.ratio})",
                        isSelected = formState.selectedRatioPresetId == preset.id,
                        onClick = { viewModel.selectRatioPreset(preset.id) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Timer Preset",
                style = MaterialTheme.typography.labelMedium,
                color = extraColors.secondaryText,
            )
            Spacer(modifier = Modifier.height(4.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SelectableChip(
                    label = "None",
                    isSelected = formState.selectedTimerPresetId == null,
                    onClick = { viewModel.selectTimerPreset(null) },
                )
                formState.timerPresets.forEach { preset ->
                    SelectableChip(
                        label = preset.preset.name,
                        isSelected = formState.selectedTimerPresetId == preset.preset.id,
                        onClick = { viewModel.selectTimerPreset(preset.preset.id) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Save button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = extraColors.gradientEnd.copy(alpha = 0.3f),
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(extraColors.gradientStart, extraColors.gradientEnd),
                        ),
                        shape = RoundedCornerShape(16.dp),
                    )
                    .clickable(enabled = !formState.isSaving) { viewModel.saveGrindSetting() },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (formState.isSaving) "Saving…" else "Save Grind Setting",
                    style = TextStyle(
                        fontFamily = DmSans,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                    ),
                    color = Color.White,
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
private fun SelectableChip(
    label: String,
    isSelected: Boolean,
    isAccent: Boolean = false,
    onClick: () -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> extraColors.gradientStart
            else -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "chipBg",
    )
    val textColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color.White
            isAccent -> MaterialTheme.colorScheme.tertiary
            else -> MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 200),
        label = "chipText",
    )
    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> Color.Transparent
            isAccent -> MaterialTheme.colorScheme.tertiary
            else -> extraColors.subtleBorder
        },
        animationSpec = tween(durationMillis = 200),
        label = "chipBorder",
    )

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(24.dp),
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(24.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = DmSans,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp,
            ),
            color = textColor,
        )
    }
}
