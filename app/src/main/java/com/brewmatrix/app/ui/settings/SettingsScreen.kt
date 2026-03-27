package com.brewmatrix.app.ui.settings

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewmatrix.app.ui.theme.BrewMatrixTheme
import com.brewmatrix.app.ui.theme.ButtonShape
import com.brewmatrix.app.ui.theme.CardShape
import com.brewmatrix.app.ui.theme.ChipShape
import com.brewmatrix.app.ui.theme.DmMono
import com.brewmatrix.app.ui.theme.InputFieldShape
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val extraColors = BrewMatrixTheme.extraColors
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle CSV export intent
    LaunchedEffect(uiState.csvExportReady) {
        if (uiState.csvExportReady && uiState.csvExportIntent != null) {
            context.startActivity(uiState.csvExportIntent)
            viewModel.clearExportState()
        }
    }

    // Show snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.dismissSnackbar()
        }
    }

    // Clear data dialogs
    when (uiState.clearDataStep) {
        ClearDataStep.FIRST_CONFIRM -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissClearData() },
                title = { Text("Clear All Data?") },
                text = {
                    Text("This will delete all brew logs, grind settings, beans, grinders, and preferences. Default presets will be restored.")
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmClearDataStep1() }) {
                        Text(
                            text = "Continue",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissClearData() }) {
                        Text("Cancel")
                    }
                },
            )
        }
        ClearDataStep.FINAL_CONFIRM -> {
            AlertDialog(
                onDismissRequest = { viewModel.dismissClearData() },
                title = { Text("Are you absolutely sure?") },
                text = {
                    Text("This action cannot be undone. All your data will be permanently deleted.")
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmClearDataFinal() }) {
                        Text(
                            text = "Delete Everything",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissClearData() }) {
                        Text("Cancel")
                    }
                },
            )
        }
        ClearDataStep.NONE -> { /* no dialog */ }
    }

    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // Header with back arrow
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }

        // ── Brewing Defaults ──
        item {
            SettingsSectionCard(delayMs = 0) {
                SectionHeader(text = "Brewing Defaults")
                Spacer(modifier = Modifier.height(16.dp))

                // Default Ratio
                Text(
                    text = "Default Ratio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extraColors.secondaryText,
                )
                Spacer(modifier = Modifier.height(8.dp))
                RatioPresetDropdown(
                    presets = uiState.ratioPresets,
                    selectedId = uiState.defaultRatioPresetId,
                    onSelect = { viewModel.setDefaultRatioPreset(it) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Default Dose
                Text(
                    text = "Default Dose (g)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = extraColors.secondaryText,
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.defaultDose,
                    onValueChange = { viewModel.setDefaultDose(it) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = InputFieldShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                        unfocusedBorderColor = extraColors.subtleBorder,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = DmMono,
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Units (non-interactive)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Units",
                        style = MaterialTheme.typography.bodyMedium,
                        color = extraColors.secondaryText,
                    )
                    Text(
                        text = "Grams",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
            }
        }

        // ── Appearance ──
        item {
            SettingsSectionCard(delayMs = 50) {
                SectionHeader(text = "Appearance")
                Spacer(modifier = Modifier.height(16.dp))

                ThemeSelector(
                    selectedMode = uiState.themeMode,
                    onSelect = { viewModel.setThemeMode(it) },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Theme preview card
                ThemePreviewCard(themeMode = uiState.themeMode)
            }
        }

        // ── Support BrewMatrix ──
        item {
            SettingsSectionCard(delayMs = 100) {
                SectionHeader(text = "Support BrewMatrix")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "BrewMatrix is free with no ads, forever. If it's improved your morning routine, consider buying me a coffee.",
                    style = MaterialTheme.typography.bodySmall,
                    color = extraColors.secondaryText,
                    lineHeight = 18.sp,
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Tip buttons with "Coming Soon"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TipButton(
                        label = "Espresso",
                        price = "$1.99",
                        gradientIntensity = 0.3f,
                        modifier = Modifier.weight(1f),
                    )
                    TipButton(
                        label = "Pour-Over",
                        price = "$4.99",
                        gradientIntensity = 0.6f,
                        modifier = Modifier.weight(1f),
                    )
                    TipButton(
                        label = "Beans",
                        price = "$9.99",
                        gradientIntensity = 1.0f,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.secondaryText.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }

        // ── Customize ──
        item {
            SettingsSectionCard(delayMs = 150) {
                SectionHeader(text = "Customize")
                Spacer(modifier = Modifier.height(16.dp))

                CosmeticUpgradeCard(
                    title = "Premium Themes",
                    description = "AMOLED Black, Ocean Blue, Warm Latte",
                    price = "$2.99",
                    previewColors = listOf(
                        Color(0xFF000000),
                        Color(0xFF1A3A5C),
                        Color(0xFF5C3D2E),
                    ),
                )

                Spacer(modifier = Modifier.height(12.dp))

                CosmeticUpgradeCard(
                    title = "Premium Icons",
                    description = "Alternate app icons to match your style",
                    price = "$2.99",
                    previewColors = listOf(
                        Color(0xFFB8860B),
                        Color(0xFF7A9E7E),
                        Color(0xFF3C2415),
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Coming Soon",
                    style = MaterialTheme.typography.labelSmall,
                    color = extraColors.secondaryText.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        }

        // ── About ──
        item {
            SettingsSectionCard(delayMs = 200) {
                SectionHeader(text = "About")
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Version",
                        style = MaterialTheme.typography.bodyMedium,
                        color = extraColors.secondaryText,
                    )
                    Text(
                        text = uiState.appVersion,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = DmMono,
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Made with caffeine and obsession",
                    style = MaterialTheme.typography.bodySmall,
                    color = extraColors.secondaryText,
                )
            }
        }

        // ── Data ──
        item {
            SettingsSectionCard(delayMs = 250) {
                SectionHeader(text = "Data")
                Spacer(modifier = Modifier.height(16.dp))

                // Export CSV
                DataActionButton(
                    label = "Export Brew Log",
                    icon = Icons.Filled.Share,
                    onClick = { viewModel.exportBrewLogCsv(context) },
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Clear All Data
                DataActionButton(
                    label = "Clear All Data",
                    icon = null,
                    isDestructive = true,
                    onClick = { viewModel.requestClearData() },
                )
            }
        }
    } // end LazyColumn

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.align(Alignment.BottomCenter),
        snackbar = { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            )
        },
    )
    } // end Box
}

// ── Reusable Components ──

@Composable
private fun SettingsSectionCard(
    delayMs: Long,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(delayMs)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = CardShape,
                    spotColor = Color(0x15000000),
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CardShape,
                )
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Column { content() }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RatioPresetDropdown(
    presets: List<com.brewmatrix.app.data.local.entity.RatioPreset>,
    selectedId: Long?,
    onSelect: (Long) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPreset = presets.find { it.id == selectedId }
    val displayText = selectedPreset?.let { "${it.name} (1:${String.format("%.3f", it.ratio)})" }
        ?: "Select a default ratio"
    val extraColors = BrewMatrixTheme.extraColors

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = displayText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = InputFieldShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = extraColors.subtleBorder,
            ),
            textStyle = MaterialTheme.typography.bodyMedium,
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            presets.forEach { preset ->
                DropdownMenuItem(
                    text = {
                        Text("${preset.name} (1:${String.format("%.3f", preset.ratio)})")
                    },
                    onClick = {
                        onSelect(preset.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun ThemeSelector(
    selectedMode: String,
    onSelect: (String) -> Unit,
) {
    val options = listOf("system" to "System", "light" to "Light", "dark" to "Dark")
    val extraColors = BrewMatrixTheme.extraColors

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (mode, label) ->
            val isSelected = selectedMode == mode
            val bgAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0f,
                animationSpec = tween(200),
                label = "themeBg",
            )
            val borderAlpha by animateFloatAsState(
                targetValue = if (isSelected) 0f else 1f,
                animationSpec = tween(200),
                label = "themeBorder",
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .then(
                        if (borderAlpha > 0f) {
                            Modifier.border(
                                width = 1.dp,
                                color = extraColors.subtleBorder.copy(alpha = borderAlpha),
                                shape = ChipShape,
                            )
                        } else Modifier
                    )
                    .then(
                        if (bgAlpha > 0f) {
                            Modifier.background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        extraColors.gradientStart.copy(alpha = bgAlpha),
                                        extraColors.gradientEnd.copy(alpha = bgAlpha),
                                    ),
                                ),
                                shape = ChipShape,
                            )
                        } else Modifier
                    )
                    .clip(ChipShape)
                    .clickable { onSelect(mode) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun ThemePreviewCard(themeMode: String) {
    val isDark = themeMode == "dark"
    val bgColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF121010) else Color(0xFFFAF7F2),
        animationSpec = tween(400),
        label = "previewBg",
    )
    val surfaceColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF1E1B18) else Color(0xFFFFFFFF),
        animationSpec = tween(400),
        label = "previewSurface",
    )
    val textColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFFF0EBE3) else Color(0xFF1A1A1A),
        animationSpec = tween(400),
        label = "previewText",
    )
    val accentColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFFD4944A) else Color(0xFFB8860B),
        animationSpec = tween(400),
        label = "previewAccent",
    )
    val gradStart by animateColorAsState(
        targetValue = if (isDark) Color(0xFFE8B86D) else Color(0xFFC17D3A),
        animationSpec = tween(400),
        label = "previewGradStart",
    )
    val gradEnd by animateColorAsState(
        targetValue = if (isDark) Color(0xFFA0693D) else Color(0xFF6B3A1F),
        animationSpec = tween(400),
        label = "previewGradEnd",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp),
    ) {
        Column {
            // Simulated surface card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(surfaceColor)
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "BrewMatrix",
                    style = MaterialTheme.typography.labelMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Accent dot
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(accentColor),
                )
                // Gradient button preview
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(gradStart, gradEnd),
                            ),
                        ),
                )
            }
        }
    }
}

@Composable
private fun TipButton(
    label: String,
    price: String,
    gradientIntensity: Float,
    modifier: Modifier = Modifier,
) {
    val extraColors = BrewMatrixTheme.extraColors
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "tipScale",
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .height(72.dp)
            .shadow(
                elevation = (4.dp * gradientIntensity),
                shape = ButtonShape,
                spotColor = extraColors.gradientEnd.copy(alpha = 0.15f * gradientIntensity),
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        extraColors.gradientStart.copy(alpha = 0.3f + 0.7f * gradientIntensity),
                        extraColors.gradientEnd.copy(alpha = 0.3f + 0.7f * gradientIntensity),
                    ),
                ),
                shape = ButtonShape,
            )
            .clip(ButtonShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.9f),
            )
            Text(
                text = price,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = DmMono,
                ),
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}

@Composable
private fun CosmeticUpgradeCard(
    title: String,
    description: String,
    price: String,
    previewColors: List<Color>,
) {
    val extraColors = BrewMatrixTheme.extraColors

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = extraColors.subtleBorder,
                shape = RoundedCornerShape(12.dp),
            )
            .clip(RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Preview mosaic
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            previewColors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(color),
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = extraColors.secondaryText,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Price pill with lock
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = extraColors.subtleBorder,
                    shape = ChipShape,
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = extraColors.secondaryText,
                )
                Text(
                    text = price,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = DmMono,
                    ),
                    color = extraColors.secondaryText,
                )
            }
        }
    }
}

@Composable
private fun DataActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "dataActionScale",
    )

    val textColor = if (isDestructive) MaterialTheme.colorScheme.error
    else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .border(
                width = 1.dp,
                color = if (isDestructive) MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
                else extraColors.subtleBorder,
                shape = ButtonShape,
            )
            .clip(ButtonShape)
            .clickable { onClick() }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                )
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = textColor,
        )
    }
}
