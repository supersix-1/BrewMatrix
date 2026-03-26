package com.brewmatrix.app.ui.brewlog

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.brewmatrix.app.data.model.BrewLogWithDetails
import com.brewmatrix.app.ui.theme.BrewMatrixTheme
import com.brewmatrix.app.ui.theme.DmMono
import com.brewmatrix.app.ui.theme.DmSans
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewLogScreen(viewModel: BrewLogViewModel) {
    val uiState by viewModel.listState.collectAsStateWithLifecycle()
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val extraColors = BrewMatrixTheme.extraColors
    var showAddSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(formState.saveSuccess) {
        if (formState.saveSuccess) {
            scope.launch { sheetState.hide() }
            showAddSheet = false
            viewModel.resetForm()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
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
                    contentDescription = "Log a brew",
                    tint = Color.White,
                )
            }
        },
    ) { paddingValues ->
        if (!uiState.isLoading && uiState.items.isEmpty()) {
            EmptyBrewLogState(
                onAddClick = { showAddSheet = true },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            )
        } else {
            BrewLogList(
                items = uiState.items,
                onDelete = { viewModel.deleteById(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
        }
    }

    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddSheet = false
                viewModel.resetForm()
            },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        ) {
            AddBrewSheet(
                formState = formState,
                onRatingChanged = viewModel::onRatingChanged,
                onNoteChanged = viewModel::onNoteChanged,
                onRatioChanged = viewModel::onRatioChanged,
                onBrewTimeChanged = viewModel::onBrewTimeChanged,
                onBeanSelected = viewModel::onBeanSelected,
                onGrinderSelected = viewModel::onGrinderSelected,
                onSave = viewModel::saveBrewLog,
                onDismiss = {
                    scope.launch { sheetState.hide() }
                    showAddSheet = false
                    viewModel.resetForm()
                },
            )
        }
    }
}

// ── List ─────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BrewLogList(
    items: List<BrewLogListItem>,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    // Track expanded entry ids
    var expandedId by remember { mutableStateOf<Long?>(null) }

    // Separate index for log entries only (for stagger delay)
    var entryCounter = 0

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(
            items = items,
            key = { _, item ->
                when (item) {
                    is BrewLogListItem.DateHeader -> "header_${item.label}"
                    is BrewLogListItem.LogEntry -> "entry_${item.details.id}"
                }
            },
        ) { _, item ->
            when (item) {
                is BrewLogListItem.DateHeader -> {
                    DateHeaderRow(label = item.label)
                }
                is BrewLogListItem.LogEntry -> {
                    val entryIndex = entryCounter++
                    AnimatedBrewLogCard(
                        entry = item.details,
                        entryIndex = entryIndex,
                        isExpanded = expandedId == item.details.id,
                        onToggle = {
                            expandedId = if (expandedId == item.details.id) null else item.details.id
                        },
                        onDelete = { onDelete(item.details.id) },
                    )
                }
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun DateHeaderRow(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = DmSans,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
        ),
        color = BrewMatrixTheme.extraColors.secondaryText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedBrewLogCard(
    entry: BrewLogWithDetails,
    entryIndex: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    val animatedAlpha = remember { Animatable(0f) }
    val animatedOffset = remember { Animatable(16f) }

    LaunchedEffect(entry.id) {
        delay(entryIndex * 50L)
        animatedAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 250, easing = EaseOutCubic),
        )
    }
    LaunchedEffect(entry.id) {
        delay(entryIndex * 50L)
        animatedOffset.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 250, easing = EaseOutCubic),
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete brew log?", style = MaterialTheme.typography.titleSmall) },
            text = { Text("This brew entry will be removed permanently.", style = MaterialTheme.typography.bodyMedium) },
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
            }
            false
        },
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 20.dp),
                )
            }
        },
        modifier = Modifier
            .alpha(animatedAlpha.value)
            .offset(y = animatedOffset.value.dp),
    ) {
        BrewLogCard(
            entry = entry,
            isExpanded = isExpanded,
            onToggle = onToggle,
        )
    }
}

@Composable
private fun BrewLogCard(
    entry: BrewLogWithDetails,
    isExpanded: Boolean,
    onToggle: () -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors
    val timeFmt = SimpleDateFormat("h:mm a", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x15000000),
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp),
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggle() },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Top row: bean/ratio + time + expand chevron ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entry.beanName ?: "Unknown Bean",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontFamily = DmSans,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "1:${String.format("%.1f", entry.ratioUsed)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = DmMono,
                                fontSize = 13.sp,
                            ),
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                        if (entry.grinderName != null) {
                            Text(
                                text = "  ·  ${entry.grinderName}",
                                style = MaterialTheme.typography.bodySmall.copy(fontFamily = DmSans),
                                color = extraColors.secondaryText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = timeFmt.format(Date(entry.brewedAt)),
                        style = MaterialTheme.typography.bodySmall.copy(fontFamily = DmMono, fontSize = 12.sp),
                        color = extraColors.secondaryText,
                    )
                    if (entry.rating != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        RatingDisplay(rating = entry.rating)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = extraColors.secondaryText,
                    modifier = Modifier.size(20.dp),
                )
            }

            // ── Expanded detail section ──
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(extraColors.subtleBorder),
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailChip(
                            label = "TIME",
                            value = formatSeconds(entry.totalBrewTimeSeconds),
                        )
                        DetailChip(
                            label = "RATIO",
                            value = "1:${String.format("%.2f", entry.ratioUsed)}",
                        )
                    }

                    if (!entry.note.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = entry.note,
                            style = MaterialTheme.typography.bodySmall.copy(fontFamily = DmSans),
                            color = extraColors.secondaryText,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailChip(label: String, value: String) {
    val extraColors = BrewMatrixTheme.extraColors
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = DmSans,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
            ),
            color = extraColors.secondaryText,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = DmMono,
                fontWeight = FontWeight.Medium,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun RatingDisplay(rating: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        for (i in 1..5) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(
                        if (i <= rating) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                    ),
            )
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%d:%02d".format(m, s)
}

// ── Empty State ───────────────────────────────────────────────────────────────

@Composable
private fun EmptyBrewLogState(onAddClick: () -> Unit, modifier: Modifier = Modifier) {
    val extraColors = BrewMatrixTheme.extraColors
    val cupColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
    val steamColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Canvas-drawn steaming cup illustration
        androidx.compose.foundation.Canvas(
            modifier = Modifier.size(100.dp),
        ) {
            val w = size.width
            val h = size.height

            // Cup body
            val cupLeft = w * 0.2f
            val cupRight = w * 0.8f
            val cupTop = h * 0.42f
            val cupBottom = h * 0.85f
            val bodyPath = Path().apply {
                moveTo(cupLeft, cupTop)
                lineTo(cupLeft + (cupRight - cupLeft) * 0.08f, cupBottom)
                lineTo(cupRight - (cupRight - cupLeft) * 0.08f, cupBottom)
                lineTo(cupRight, cupTop)
                close()
            }
            drawPath(bodyPath, color = cupColor)

            // Saucer
            drawOval(
                color = cupColor,
                topLeft = Offset(w * 0.12f, cupBottom - h * 0.05f),
                size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.1f),
            )

            // Handle
            val handlePath = Path().apply {
                moveTo(cupRight, cupTop + (cupBottom - cupTop) * 0.15f)
                cubicTo(
                    w * 0.95f, cupTop + (cupBottom - cupTop) * 0.15f,
                    w * 0.95f, cupTop + (cupBottom - cupTop) * 0.65f,
                    cupRight, cupTop + (cupBottom - cupTop) * 0.65f,
                )
            }
            drawPath(handlePath, color = cupColor, style = Stroke(width = h * 0.06f, cap = StrokeCap.Round))

            // Steam wisps
            val steamStroke = Stroke(width = h * 0.03f, cap = StrokeCap.Round)
            for (i in 0..2) {
                val cx = w * (0.32f + i * 0.18f)
                val steamPath = Path().apply {
                    moveTo(cx, cupTop - h * 0.04f)
                    cubicTo(cx - w * 0.04f, cupTop - h * 0.13f, cx + w * 0.04f, cupTop - h * 0.22f, cx, cupTop - h * 0.32f)
                }
                drawPath(steamPath, color = steamColor, style = steamStroke)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "No brews logged yet",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = DmSans,
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tap the button below to log your first brew",
            style = MaterialTheme.typography.bodyMedium.copy(fontFamily = DmSans),
            color = extraColors.secondaryText,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.25f),
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(extraColors.gradientStart, extraColors.gradientEnd),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
                .clip(RoundedCornerShape(16.dp))
                .clickable { onAddClick() }
                .padding(horizontal = 32.dp, vertical = 14.dp),
        ) {
            Text(
                text = "Log your first brew",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = DmSans,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Color.White,
            )
        }
    }
}

// ── Add Brew Bottom Sheet ─────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBrewSheet(
    formState: AddBrewFormState,
    onRatingChanged: (Int) -> Unit,
    onNoteChanged: (String) -> Unit,
    onRatioChanged: (String) -> Unit,
    onBrewTimeChanged: (String) -> Unit,
    onBeanSelected: (Long?) -> Unit,
    onGrinderSelected: (Long?) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val extraColors = BrewMatrixTheme.extraColors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
    ) {
        Text(
            text = "Log a Brew",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = DmSans,
                fontWeight = FontWeight.Bold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Rating picker
        Text(
            text = "RATING",
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = DmSans,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
            ),
            color = extraColors.secondaryText,
        )
        Spacer(modifier = Modifier.height(8.dp))
        RatingPicker(
            rating = formState.rating,
            onRatingChanged = onRatingChanged,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ratio
        OutlinedTextField(
            value = formState.ratioUsed,
            onValueChange = onRatioChanged,
            label = { Text("Ratio (e.g. 15.0)") },
            isError = formState.ratioError != null,
            supportingText = formState.ratioError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = DmMono),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Brew time (seconds)
        OutlinedTextField(
            value = formState.totalBrewTimeSeconds,
            onValueChange = onBrewTimeChanged,
            label = { Text("Total time (seconds)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = DmMono),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bean dropdown
        if (formState.beans.isNotEmpty()) {
            BeanDropdown(
                beans = formState.beans,
                selectedBeanId = formState.selectedBeanId,
                onBeanSelected = onBeanSelected,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Grinder dropdown
        if (formState.grinders.isNotEmpty()) {
            GrinderDropdown(
                grinders = formState.grinders,
                selectedGrinderId = formState.selectedGrinderId,
                onGrinderSelected = onGrinderSelected,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Note
        OutlinedTextField(
            value = formState.note,
            onValueChange = onNoteChanged,
            label = { Text("Notes (optional)") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = DmSans),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Save button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                )
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            BrewMatrixTheme.extraColors.gradientStart,
                            BrewMatrixTheme.extraColors.gradientEnd,
                        ),
                    ),
                    shape = RoundedCornerShape(16.dp),
                )
                .clip(RoundedCornerShape(16.dp))
                .clickable(enabled = !formState.isSaving) { onSave() },
            contentAlignment = Alignment.Center,
        ) {
            AnimatedContent(
                targetState = formState.isSaving,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "save_btn",
            ) { saving ->
                Text(
                    text = if (saving) "Saving…" else "Save Brew",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = DmSans,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "Cancel",
                style = MaterialTheme.typography.bodyMedium.copy(fontFamily = DmSans),
                color = BrewMatrixTheme.extraColors.secondaryText,
            )
        }
    }
}

@Composable
private fun RatingPicker(
    rating: Int?,
    onRatingChanged: (Int) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        for (i in 1..5) {
            val filled = rating != null && i <= rating
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        if (filled) MaterialTheme.colorScheme.tertiary
                        else Color.Transparent,
                    )
                    .border(
                        width = 2.dp,
                        color = if (filled) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.35f),
                        shape = CircleShape,
                    )
                    .clickable { onRatingChanged(i) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$i",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = DmMono,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = if (filled) Color.White else MaterialTheme.colorScheme.tertiary,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BeanDropdown(
    beans: List<com.brewmatrix.app.data.local.entity.Bean>,
    selectedBeanId: Long?,
    onBeanSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = beans.firstOrNull { it.id == selectedBeanId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selected?.name ?: "None",
            onValueChange = {},
            readOnly = true,
            label = { Text("Bean (optional)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onBeanSelected(null)
                    expanded = false
                },
            )
            beans.forEach { bean ->
                DropdownMenuItem(
                    text = { Text(bean.name) },
                    onClick = {
                        onBeanSelected(bean.id)
                        expanded = false
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GrinderDropdown(
    grinders: List<com.brewmatrix.app.data.local.entity.Grinder>,
    selectedGrinderId: Long?,
    onGrinderSelected: (Long?) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = grinders.firstOrNull { it.id == selectedGrinderId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            value = selected?.name ?: "None",
            onValueChange = {},
            readOnly = true,
            label = { Text("Grinder (optional)") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onGrinderSelected(null)
                    expanded = false
                },
            )
            grinders.forEach { grinder ->
                DropdownMenuItem(
                    text = { Text(grinder.name) },
                    onClick = {
                        onGrinderSelected(grinder.id)
                        expanded = false
                    },
                )
            }
        }
    }
}
