package com.brewmatrix.app.ui.grindmemory

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewmatrix.app.data.model.GrindSettingWithDetails
import com.brewmatrix.app.ui.theme.BrewMatrixTheme
import com.brewmatrix.app.ui.theme.DmMono
import com.brewmatrix.app.ui.theme.DmSans
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrindMemoryScreen(
    viewModel: GrindMemoryViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToCalculator: (ratioPresetId: Long?) -> Unit,
) {
    val uiState by viewModel.listState.collectAsStateWithLifecycle()
    val extraColors = BrewMatrixTheme.extraColors

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // FAB with gradient fill, rounded square, glow shadow
            FloatingActionButton(
                onClick = {
                    viewModel.resetForm()
                    onNavigateToAdd()
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.Transparent,
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
                    )
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(extraColors.gradientStart, extraColors.gradientEnd),
                        ),
                        shape = RoundedCornerShape(20.dp),
                    ),
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add grind setting",
                    tint = Color.White,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Search bar with animated focus border
            SearchBar(
                query = uiState.searchQuery,
                onQueryChanged = viewModel::onSearchQueryChanged,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!uiState.isLoading && uiState.settings.isEmpty() && uiState.searchQuery.isBlank()) {
                // Empty state
                EmptyState(onAddClick = {
                    viewModel.resetForm()
                    onNavigateToAdd()
                })
            } else {
                // Settings list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(
                        items = uiState.settings,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        AnimatedGrindCard(
                            item = item,
                            index = index,
                            onBrewClick = {
                                viewModel.updateLastUsed(item.id)
                                onNavigateToCalculator(item.linkedRatioPresetId)
                            },
                            onDelete = { viewModel.deleteSettingById(item.id) },
                        )
                    }
                    // Bottom spacing so FAB doesn't cover last card
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val extraColors = BrewMatrixTheme.extraColors
    val borderColor = if (isFocused) {
        MaterialTheme.colorScheme.tertiary
    } else {
        extraColors.subtleBorder
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp),
                spotColor = Color(0x10000000),
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
            )
            .background(
                color = Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(borderColor.copy(alpha = 0.3f), borderColor.copy(alpha = 0.1f)),
                ),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(1.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(11.dp),
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = extraColors.secondaryText,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search beans, grinders, settings…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = extraColors.secondaryText,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    textStyle = TextStyle(
                        fontFamily = DmSans,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    singleLine = true,
                    interactionSource = interactionSource,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedGrindCard(
    item: GrindSettingWithDetails,
    index: Int,
    onBrewClick: () -> Unit,
    onDelete: () -> Unit,
) {
    // Staggered fade-up animation
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(16f) }

    LaunchedEffect(item.id) {
        delay(index * 50L)
        animatedAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 250, easing = EaseOutCubic),
        )
    }
    LaunchedEffect(item.id) {
        delay(index * 50L)
        animatedOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 250, easing = EaseOutCubic),
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete grind setting?",
                    style = MaterialTheme.typography.titleSmall,
                )
            },
            text = {
                Text(
                    "This will remove the grind setting for ${item.beanName} on ${item.grinderName}.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDeleteDialog = true
                false // Don't dismiss yet, wait for dialog
            } else {
                false
            }
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // Red delete background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 24.dp),
                )
            }
        },
        modifier = Modifier
            .alpha(animatedAlpha.value)
            .offset(y = animatedOffset.value.dp),
        enableDismissFromStartToEnd = false,
    ) {
        GrindSettingCard(
            item = item,
            onBrewClick = onBrewClick,
        )
    }
}

@Composable
private fun GrindSettingCard(
    item: GrindSettingWithDetails,
    onBrewClick: () -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x15000000),
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    ),
                    radius = 400f,
                ),
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Bean name — bold, primary
                    Text(
                        text = item.beanName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (item.beanRoaster != null) {
                        Text(
                            text = item.beanRoaster,
                            style = MaterialTheme.typography.bodySmall,
                            color = extraColors.secondaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    // Grinder name
                    Text(
                        text = item.grinderName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = extraColors.secondaryText,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                // Last used date
                Text(
                    text = formatDate(item.lastUsedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = extraColors.secondaryText.copy(alpha = 0.7f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Grind setting pill badge
                Box(
                    modifier = Modifier
                        .background(
                            color = extraColors.subtleBorder.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(24.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = item.setting,
                        style = TextStyle(
                            fontFamily = DmMono,
                            fontWeight = FontWeight.Normal,
                            fontSize = 14.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }

                // "Brew" pill button
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = extraColors.gradientEnd.copy(alpha = 0.2f),
                        )
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    extraColors.gradientStart,
                                    extraColors.gradientEnd,
                                ),
                            ),
                            shape = RoundedCornerShape(24.dp),
                        )
                        .clickable(onClick = onBrewClick)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Brew",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Brew",
                            style = TextStyle(
                                fontFamily = DmSans,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                            ),
                            color = Color.White,
                        )
                    }
                }
            }

            // Notes preview if present
            if (!item.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = extraColors.secondaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(onAddClick: () -> Unit) {
    val extraColors = BrewMatrixTheme.extraColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Coffee grinder illustration using Canvas
        androidx.compose.foundation.Canvas(
            modifier = Modifier.size(120.dp),
        ) {
            val primaryColor = extraColors.gradientStart
            val strokeWidth = 2.5f

            // Grinder body
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.3f),
                topLeft = Offset(size.width * 0.25f, size.height * 0.35f),
                size = androidx.compose.ui.geometry.Size(
                    size.width * 0.5f,
                    size.height * 0.55f,
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            // Grinder top/hopper
            val hopperPath = Path().apply {
                moveTo(size.width * 0.3f, size.height * 0.35f)
                lineTo(size.width * 0.2f, size.height * 0.15f)
                lineTo(size.width * 0.8f, size.height * 0.15f)
                lineTo(size.width * 0.7f, size.height * 0.35f)
            }
            drawPath(
                path = hopperPath,
                color = primaryColor.copy(alpha = 0.3f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            // Handle
            drawLine(
                color = primaryColor.copy(alpha = 0.4f),
                start = Offset(size.width * 0.5f, size.height * 0.15f),
                end = Offset(size.width * 0.5f, size.height * 0.05f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
            drawCircle(
                color = primaryColor.copy(alpha = 0.4f),
                radius = 4f,
                center = Offset(size.width * 0.5f, size.height * 0.05f),
                style = Stroke(width = strokeWidth),
            )

            // Handle arm
            drawLine(
                color = primaryColor.copy(alpha = 0.4f),
                start = Offset(size.width * 0.5f, size.height * 0.05f),
                end = Offset(size.width * 0.75f, size.height * 0.08f),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )

            // Steam wisps
            val steamPath1 = Path().apply {
                moveTo(size.width * 0.35f, size.height * 0.12f)
                cubicTo(
                    size.width * 0.3f, size.height * 0.05f,
                    size.width * 0.4f, size.height * 0.0f,
                    size.width * 0.35f, -size.height * 0.05f,
                )
            }
            drawPath(
                path = steamPath1,
                color = primaryColor.copy(alpha = 0.15f),
                style = Stroke(width = 1.5f, cap = StrokeCap.Round),
            )
            val steamPath2 = Path().apply {
                moveTo(size.width * 0.45f, size.height * 0.1f)
                cubicTo(
                    size.width * 0.5f, size.height * 0.03f,
                    size.width * 0.4f, -size.height * 0.02f,
                    size.width * 0.45f, -size.height * 0.08f,
                )
            }
            drawPath(
                path = steamPath2,
                color = primaryColor.copy(alpha = 0.15f),
                style = Stroke(width = 1.5f, cap = StrokeCap.Round),
            )

            // Drawer at bottom
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.25f),
                topLeft = Offset(size.width * 0.3f, size.height * 0.78f),
                size = androidx.compose.ui.geometry.Size(
                    size.width * 0.4f,
                    size.height * 0.08f,
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "No beans in the memory yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap + to save your first grind setting",
            style = MaterialTheme.typography.bodyMedium,
            color = extraColors.secondaryText,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Gradient CTA button
        Box(
            modifier = Modifier
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
                .clickable(onClick = onAddClick)
                .padding(horizontal = 32.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Add First Setting",
                style = TextStyle(
                    fontFamily = DmSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                color = Color.White,
            )
        }
    }
}

private fun formatDate(epochMillis: Long): String {
    val formatter = SimpleDateFormat("MMM d", Locale.getDefault())
    return formatter.format(Date(epochMillis))
}
