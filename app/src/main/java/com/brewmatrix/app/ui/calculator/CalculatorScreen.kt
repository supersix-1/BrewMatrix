package com.brewmatrix.app.ui.calculator

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewmatrix.app.data.local.entity.RatioPreset
import com.brewmatrix.app.ui.theme.BrewMatrixTheme
import com.brewmatrix.app.ui.theme.ButtonShape
import com.brewmatrix.app.ui.theme.CardShape
import com.brewmatrix.app.ui.theme.ChipShape
import com.brewmatrix.app.ui.theme.DmMono
import kotlinx.coroutines.delay

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    onNavigateToTimer: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Brew Calculator",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Ratio Preset Chips
        RatioPresetChips(
            presets = uiState.presets,
            activePresetId = uiState.activePresetId,
            onPresetSelected = viewModel::onPresetSelected,
            onAddPreset = viewModel::onShowAddPresetDialog,
            onEditPreset = viewModel::onShowEditPresetDialog,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Coffee Input Card
        BrewInputCard(
            label = "Coffee",
            displayValue = uiState.displayCoffee,
            textFieldValue = uiState.coffeeGrams,
            onValueChange = viewModel::onCoffeeChanged,
        )

        // Ratio Divider
        RatioDivider(ratio = uiState.activeRatio)

        // Water Input Card
        BrewInputCard(
            label = "Water",
            displayValue = uiState.displayWater,
            textFieldValue = uiState.waterGrams,
            onValueChange = viewModel::onWaterChanged,
        )

        // Error message
        AnimatedVisibility(visible = uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Start Brew Button
        StartBrewButton(onClick = onNavigateToTimer)

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Dialogs
    if (uiState.showAddPresetDialog) {
        PresetDialog(
            title = "New Ratio Preset",
            initialName = "",
            initialRatio = "",
            onConfirm = { name, ratio ->
                ratio.toDoubleOrNull()?.let { viewModel.onSavePreset(name, it) }
            },
            onDismiss = viewModel::onDismissAddPresetDialog,
        )
    }

    if (uiState.showEditPresetDialog && uiState.editingPreset != null) {
        val preset = uiState.editingPreset!!
        PresetDialog(
            title = "Edit Preset",
            initialName = preset.name,
            initialRatio = preset.ratio.toString(),
            onConfirm = { name, ratio ->
                ratio.toDoubleOrNull()?.let { viewModel.onEditPreset(preset.id, name, it) }
            },
            onDismiss = viewModel::onDismissEditPresetDialog,
            showDelete = true,
            onDelete = {
                viewModel.onDismissEditPresetDialog()
                viewModel.onShowDeletePresetDialog(preset)
            },
        )
    }

    if (uiState.showDeletePresetDialog && uiState.editingPreset != null) {
        val preset = uiState.editingPreset!!
        AlertDialog(
            onDismissRequest = viewModel::onDismissDeletePresetDialog,
            title = { Text("Delete Preset") },
            text = { Text("Delete \"${preset.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onDeletePreset(preset.id) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissDeletePresetDialog) {
                    Text("Cancel")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RatioPresetChips(
    presets: List<RatioPreset>,
    activePresetId: Long?,
    onPresetSelected: (Long) -> Unit,
    onAddPreset: () -> Unit,
    onEditPreset: (RatioPreset) -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(
            items = presets,
            key = { _, preset -> preset.id },
        ) { index, preset ->
            val isActive = preset.id == activePresetId

            // Staggered fade-up animation
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(index * 50L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(250)) +
                    slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(250),
                    ),
            ) {
                val gradientAlpha by animateFloatAsState(
                    targetValue = if (isActive) 1f else 0f,
                    animationSpec = tween(durationMillis = 200),
                    label = "chipGradient",
                )
                val borderAlpha by animateFloatAsState(
                    targetValue = if (isActive) 0f else 1f,
                    animationSpec = tween(durationMillis = 200),
                    label = "chipBorder",
                )

                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .combinedClickable(
                            onClick = { onPresetSelected(preset.id) },
                            onLongClick = {
                                if (!preset.isDefault) onEditPreset(preset)
                            },
                        )
                        .drawBehind {
                            if (gradientAlpha > 0f) {
                                drawRoundRect(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            extraColors.gradientStart.copy(alpha = gradientAlpha),
                                            extraColors.gradientEnd.copy(alpha = gradientAlpha),
                                        ),
                                    ),
                                    cornerRadius = CornerRadius(24.dp.toPx()),
                                )
                            }
                        }
                        .then(
                            if (borderAlpha > 0f) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = extraColors.subtleBorder.copy(alpha = borderAlpha),
                                    shape = ChipShape,
                                )
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = preset.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isActive) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // "+" Add chip
        item {
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(presets.size * 50L)
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(250)) +
                    slideInVertically(
                        initialOffsetY = { it / 4 },
                        animationSpec = tween(250),
                    ),
            ) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .border(
                            width = 1.dp,
                            color = BrewMatrixTheme.extraColors.subtleBorder,
                            shape = ChipShape,
                        )
                        .combinedClickable(onClick = onAddPreset)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add preset",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun BrewInputCard(
    label: String,
    displayValue: Double,
    textFieldValue: String,
    onValueChange: (String) -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors

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
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Animated number display with underlying text field
                Box(modifier = Modifier.weight(1f)) {
                    // AnimatedContent for the visual display
                    AnimatedContent(
                        targetState = displayValue,
                        transitionSpec = {
                            (slideInVertically(
                                initialOffsetY = { height -> height / 3 },
                                animationSpec = tween(150),
                            ) + fadeIn(animationSpec = tween(150)))
                                .togetherWith(
                                    slideOutVertically(
                                        targetOffsetY = { height -> -height / 3 },
                                        animationSpec = tween(150),
                                    ) + fadeOut(animationSpec = tween(150))
                                )
                        },
                        label = "${label}NumberAnim",
                    ) { _ ->
                        // Invisible — real input is the BasicTextField below
                    }

                    BasicTextField(
                        value = textFieldValue,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(
                            fontFamily = DmMono,
                            fontWeight = FontWeight.Normal,
                            fontSize = 48.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                        ),
                        singleLine = true,
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "g",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun RatioDivider(ratio: Double) {
    val extraColors = BrewMatrixTheme.extraColors
    val ratioText = if (ratio == ratio.toLong().toDouble()) {
        "1 : ${ratio.toLong()}"
    } else {
        "1 : $ratio"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 0.5.dp,
            color = extraColors.subtleBorder,
        )

        Text(
            text = ratioText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 0.5.dp,
            color = extraColors.subtleBorder,
        )
    }
}

@Composable
private fun StartBrewButton(onClick: () -> Unit) {
    val extraColors = BrewMatrixTheme.extraColors
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f,
        ),
        label = "buttonScale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = 8.dp,
                shape = ButtonShape,
                spotColor = extraColors.gradientEnd.copy(alpha = 0.3f),
            )
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(extraColors.gradientStart, extraColors.gradientEnd),
                ),
                shape = ButtonShape,
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                        onClick()
                    },
                )
            }
            .height(56.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Start Brew",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
            ),
            color = Color.White,
        )
    }
}

@Composable
private fun PresetDialog(
    title: String,
    initialName: String,
    initialRatio: String,
    onConfirm: (name: String, ratio: String) -> Unit,
    onDismiss: () -> Unit,
    showDelete: Boolean = false,
    onDelete: (() -> Unit)? = null,
) {
    var name by remember { mutableStateOf(initialName) }
    var ratio by remember { mutableStateOf(initialRatio) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var ratioError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it.take(100)
                        nameError = null
                    },
                    label = { Text("Name") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ratio,
                    onValueChange = { input ->
                        ratio = input.filter { it.isDigit() || it == '.' }
                        ratioError = null
                    },
                    label = { Text("Ratio (e.g., 16.667)") },
                    isError = ratioError != null,
                    supportingText = ratioError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                    ),
                )

                if (showDelete && onDelete != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onDelete) {
                        Text("Delete this preset", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmedName = name.trim()
                    val ratioValue = ratio.toDoubleOrNull()

                    var hasError = false
                    if (trimmedName.isBlank()) {
                        nameError = "Name cannot be empty"
                        hasError = true
                    }
                    if (ratioValue == null || ratioValue !in 1.0..99.999) {
                        ratioError = "Ratio must be between 1 and 99.999"
                        hasError = true
                    }

                    if (!hasError) {
                        onConfirm(trimmedName, ratio)
                    }
                },
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
