package com.brewmatrix.app.ui.timer

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewmatrix.app.data.local.entity.TimerPhase
import kotlinx.coroutines.delay
import com.brewmatrix.app.ui.theme.BrewMatrixTheme
import com.brewmatrix.app.ui.theme.ButtonShape
import com.brewmatrix.app.ui.theme.CardShape
import com.brewmatrix.app.ui.theme.ChipShape
import com.brewmatrix.app.ui.theme.DmMono

@Composable
fun TimerScreen(viewModel: TimerViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val timerBackground = if (isDark) Color(0xFF0E0C0A) else Color(0xFFF2EDE5)

    // Haptic side effects
    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val manager = context.getSystemService(VibratorManager::class.java)
                manager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Vibrator::class.java)
            }

            when (effect) {
                is TimerSideEffect.PhaseTransition -> {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE),
                    )
                }
                is TimerSideEffect.Completed -> {
                    vibrator.vibrate(
                        VibrationEffect.createWaveform(
                            longArrayOf(0, 200, 100, 200, 100, 200),
                            -1,
                        ),
                    )
                }
            }
        }
    }

    // Release wake lock if composable leaves composition
    DisposableEffect(Unit) {
        onDispose {
            // ViewModel.onCleared handles this, but extra safety
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(timerBackground)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Preset selector chips
        PresetSelector(
            presets = uiState.presets,
            activePresetId = uiState.activePresetId,
            isTimerActive = uiState.timerState == TimerState.RUNNING || uiState.timerState == TimerState.PAUSED,
            onPresetSelected = viewModel::onPresetSelected,
        )

        Spacer(modifier = Modifier.weight(0.5f))

        // Phase name
        if (uiState.phases.isNotEmpty()) {
            val currentPhase = uiState.phases.getOrNull(uiState.currentPhaseIndex)
            AnimatedContent(
                targetState = currentPhase?.phaseName ?: "",
                transitionSpec = {
                    (fadeIn(tween(200)) + slideInVertically { -it / 4 })
                        .togetherWith(fadeOut(tween(200)) + slideOutVertically { it / 4 })
                },
                label = "phaseName",
            ) { name ->
                Text(
                    text = name.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    ),
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Countdown display
        CountdownDisplay(
            timerState = uiState.timerState,
            remainingMillis = uiState.currentPhaseRemainingMillis,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Phase progress bar
        if (uiState.phases.isNotEmpty() && uiState.timerState != TimerState.COMPLETED) {
            val currentPhase = uiState.phases.getOrNull(uiState.currentPhaseIndex)
            if (currentPhase != null) {
                PhaseProgressBar(
                    totalMillis = currentPhase.durationSeconds * 1000L,
                    remainingMillis = uiState.currentPhaseRemainingMillis,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Phase chips
        if (uiState.phases.isNotEmpty()) {
            PhaseChips(
                phases = uiState.phases,
                currentPhaseIndex = uiState.currentPhaseIndex,
                timerState = uiState.timerState,
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Water target
        val currentPhase = uiState.phases.getOrNull(uiState.currentPhaseIndex)
        AnimatedVisibility(
            visible = currentPhase?.targetWaterGrams != null &&
                uiState.timerState != TimerState.COMPLETED,
            enter = fadeIn(tween(200)) + slideInVertically { it / 3 },
            exit = fadeOut(tween(200)),
        ) {
            currentPhase?.targetWaterGrams?.let { target ->
                WaterTarget(targetGrams = target)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Controls
        ControlsRow(
            timerState = uiState.timerState,
            hasPhases = uiState.phases.isNotEmpty(),
            onPlayPause = viewModel::onPlayPause,
            onReset = viewModel::onReset,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Total elapsed
        val totalSeconds = (uiState.totalElapsedMillis / 1000).toInt()
        Text(
            text = "Total: ${formatTime(totalSeconds * 1000L)}",
            style = MaterialTheme.typography.bodySmall.copy(fontFamily = DmMono),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PresetSelector(
    presets: List<com.brewmatrix.app.data.model.TimerPresetWithPhases>,
    activePresetId: Long?,
    isTimerActive: Boolean,
    onPresetSelected: (Long) -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors

    if (presets.isEmpty()) {
        Text(
            text = "No timer presets saved — create one to get started",
            style = MaterialTheme.typography.bodySmall,
            color = extraColors.secondaryText,
            modifier = Modifier.padding(vertical = 10.dp),
        )
    } else {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(
                items = presets,
                key = { _, preset -> preset.preset.id },
            ) { index, preset ->
                val isActive = preset.preset.id == activePresetId

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
                        animationSpec = tween(200),
                        label = "presetChipGradient",
                    )

                    Box(
                        modifier = Modifier
                            .widthIn(max = 160.dp)
                            .height(40.dp)
                            .then(
                                if (!isTimerActive) {
                                    Modifier.clickable { onPresetSelected(preset.preset.id) }
                                } else {
                                    Modifier
                                }
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
                                if (gradientAlpha < 1f) {
                                    Modifier.border(
                                        width = 1.dp,
                                        color = extraColors.subtleBorder.copy(alpha = 1f - gradientAlpha),
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
                            text = preset.preset.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isActive) Color.White
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownDisplay(
    timerState: TimerState,
    remainingMillis: Long,
) {
    val displayText = if (timerState == TimerState.COMPLETED) {
        "Done"
    } else {
        formatTime(remainingMillis)
    }

    // Subtle seconds pulse
    val seconds = (remainingMillis / 1000).toInt()
    val pulseScale by animateFloatAsState(
        targetValue = if (timerState == TimerState.RUNNING) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 600f),
        label = "secondsPulse",
    )

    // Completion glow
    val completionGlow by animateFloatAsState(
        targetValue = if (timerState == TimerState.COMPLETED) 0.3f else 0f,
        animationSpec = tween(600),
        label = "completionGlow",
    )

    val extraColors = BrewMatrixTheme.extraColors

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                if (completionGlow > 0f) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                extraColors.gradientStart.copy(alpha = completionGlow),
                                Color.Transparent,
                            ),
                            center = center,
                            radius = size.minDimension,
                        ),
                    )
                }
            },
    ) {
        AnimatedContent(
            targetState = displayText,
            transitionSpec = {
                if (targetState == "Done") {
                    (scaleIn(initialScale = 0.8f, animationSpec = tween(300)) + fadeIn(tween(300)))
                        .togetherWith(fadeOut(tween(150)))
                } else {
                    (fadeIn(tween(50))).togetherWith(fadeOut(tween(50)))
                }
            },
            label = "countdown",
        ) { text ->
            Text(
                text = text,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer {
                    if (timerState == TimerState.RUNNING) {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                },
            )
        }
    }
}

@Composable
private fun PhaseProgressBar(
    totalMillis: Long,
    remainingMillis: Long,
) {
    val extraColors = BrewMatrixTheme.extraColors
    val progress by remember(totalMillis, remainingMillis) {
        derivedStateOf {
            if (totalMillis > 0) {
                1f - (remainingMillis.toFloat() / totalMillis.toFloat())
            } else 0f
        }
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(100),
        label = "progressBar",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(
                color = extraColors.subtleBorder,
                shape = CardShape,
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = animatedProgress)
                .height(4.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(extraColors.gradientStart, extraColors.gradientEnd),
                    ),
                    shape = CardShape,
                ),
        )
    }
}

@Composable
private fun PhaseChips(
    phases: List<TimerPhase>,
    currentPhaseIndex: Int,
    timerState: TimerState,
) {
    val extraColors = BrewMatrixTheme.extraColors

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(
            items = phases,
            key = { _, phase -> phase.id },
        ) { index, phase ->
            val isActive = index == currentPhaseIndex && timerState != TimerState.COMPLETED
            val isCompleted = index < currentPhaseIndex || timerState == TimerState.COMPLETED

            val gradientAlpha by animateFloatAsState(
                targetValue = if (isActive) 1f else 0f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                label = "phaseChip$index",
            )

            val scale by animateFloatAsState(
                targetValue = if (isActive) 1f else 0.95f,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
                label = "phaseChipScale$index",
            )

            Box(
                modifier = Modifier
                    .widthIn(max = 120.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .height(36.dp)
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
                        when {
                            isActive -> Modifier
                            isCompleted -> Modifier.background(
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                shape = ChipShape,
                            )
                            else -> Modifier.border(
                                width = 1.dp,
                                color = extraColors.subtleBorder,
                                shape = ChipShape,
                            )
                        }
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = phase.phaseName,
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        isActive -> Color.White
                        isCompleted -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun WaterTarget(targetGrams: Double) {
    val extraColors = BrewMatrixTheme.extraColors

    Box(
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = CardShape,
                spotColor = Color(0x10000000),
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CardShape,
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Pour to",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${targetGrams.toInt()}g",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ControlsRow(
    timerState: TimerState,
    hasPhases: Boolean,
    onPlayPause: () -> Unit,
    onReset: () -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors
    val showReset = timerState != TimerState.READY

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        // Reset button
        AnimatedVisibility(
            visible = showReset,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200)),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        shape = CircleShape,
                    )
                    .clickable { onReset() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        if (showReset) {
            Spacer(modifier = Modifier.width(32.dp))
        }

        // Play/Pause button
        val isRunning = timerState == TimerState.RUNNING
        val buttonGradientAlpha by animateFloatAsState(
            targetValue = if (isRunning) 0f else 1f,
            animationSpec = tween(200),
            label = "playPauseGradient",
        )

        Box(
            modifier = Modifier
                .size(64.dp)
                .then(
                    if (timerState != TimerState.COMPLETED) {
                        Modifier.clickable { onPlayPause() }
                    } else {
                        Modifier
                    }
                )
                .drawBehind {
                    if (buttonGradientAlpha > 0f) {
                        drawCircle(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    extraColors.gradientStart.copy(alpha = buttonGradientAlpha),
                                    extraColors.gradientEnd.copy(alpha = buttonGradientAlpha),
                                ),
                            ),
                        )
                    }
                }
                .then(
                    if (isRunning) {
                        Modifier.background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = CircleShape,
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Play",
                tint = if (isRunning) MaterialTheme.colorScheme.primary else Color.White,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).toInt().coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
