package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ContentPlatform
import com.example.model.ContentStatus
import com.example.model.DefaultHookTemplates
import com.example.model.GermanDateFormat
import com.example.model.HookStyle
import com.example.model.SimpleDate
import com.example.viewmodel.EditorDraft
import kotlin.math.ceil

private val TimePresets = listOf("09:00", "12:00", "16:00", "18:00", "20:00")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentEditorSheet(
    draft: EditorDraft,
    onDraftChange: (EditorDraft) -> Unit,
    onDismiss: () -> Unit,
    onSave: (EditorDraft) -> Unit,
    onDelete: (Long) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showTitleError by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

    val isEditing = draft.editingId != 0L
    val hookWords = remember(draft.hook) {
        val trimmed = draft.hook.trim()
        if (trimmed.isEmpty()) 0 else trimmed.split(Regex("\\s+")).size
    }
    val hookSeconds = remember(hookWords) {
        if (hookWords == 0) 0 else ceil(hookWords / 2.4).toInt().coerceAtLeast(1)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("content_editor_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
        ) {
            // Top bar of modal sheet
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        if (isEditing) R.string.editor_edit_title else R.string.editor_new_title
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isEditing) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.testTag("editor_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = stringResource(R.string.editor_delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("editor_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = stringResource(R.string.cd_close)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Title field
                OutlinedTextField(
                    value = draft.title,
                    onValueChange = {
                        onDraftChange(draft.copy(title = it))
                        if (showTitleError && it.isNotBlank()) showTitleError = false
                    },
                    label = { Text(stringResource(R.string.editor_field_title)) },
                    placeholder = { Text(stringResource(R.string.editor_field_title_placeholder)) },
                    isError = showTitleError,
                    supportingText = if (showTitleError) {
                        { Text(stringResource(R.string.editor_title_error)) }
                    } else {
                        null
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_title_input")
                )

                // Platform selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.editor_field_platform),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(end = 4.dp)
                    ) {
                        items(ContentPlatform.entries, key = { it.name }) { itemPlatform ->
                            val selected = draft.platform == itemPlatform
                            FilterChip(
                                selected = selected,
                                onClick = { onDraftChange(draft.copy(platform = itemPlatform)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = itemPlatform.icon,
                                        contentDescription = null,
                                        tint = if (selected) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            itemPlatform.accentColor
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(itemPlatform.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.testTag("editor_platform_${itemPlatform.name.lowercase()}")
                            )
                        }
                    }
                }

                // Video Hook field + live spoken time badge + quick formula pills
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.editor_field_hook),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (hookWords > 0) {
                            Surface(
                                shape = CircleShape,
                                color = if (hookSeconds <= 5) {
                                    MaterialTheme.colorScheme.tertiaryContainer
                                } else {
                                    MaterialTheme.colorScheme.secondaryContainer
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Timer,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Text(
                                        text = stringResource(
                                            R.string.hook_duration_estimate,
                                            hookSeconds,
                                            hookWords
                                        ),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = draft.hook,
                        onValueChange = { onDraftChange(draft.copy(hook = it)) },
                        placeholder = { Text(stringResource(R.string.editor_field_hook_placeholder)) },
                        minLines = 3,
                        maxLines = 5,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("editor_hook_input")
                    )

                    // Hook style chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(HookStyle.entries, key = { it.name }) { style ->
                            FilterChip(
                                selected = draft.hookStyle == style,
                                onClick = { onDraftChange(draft.copy(hookStyle = style)) },
                                label = { Text(style.badgeText) },
                                modifier = Modifier.testTag("editor_hook_style_${style.name.lowercase()}")
                            )
                        }
                    }

                    // Quick starter formula chips when hook is empty
                    if (draft.hook.isBlank()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(DefaultHookTemplates.take(3), key = { it.id }) { template ->
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier
                                        .clickable {
                                            onDraftChange(
                                                draft.copy(
                                                    hook = template.formula,
                                                    hookStyle = template.style
                                                )
                                            )
                                        }
                                        .testTag("editor_quick_template_${template.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.AutoAwesome,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = template.title,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Status & Progress workflow selector
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.editor_field_status),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(ContentStatus.entries, key = { it.name }) { itemStatus ->
                            FilterChip(
                                selected = draft.status == itemStatus,
                                onClick = { onDraftChange(draft.copy(status = itemStatus)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = itemStatus.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                label = { Text(itemStatus.displayName) },
                                modifier = Modifier.testTag("editor_status_${itemStatus.name.lowercase()}")
                            )
                        }
                    }
                }

                // Schedule Date & Time (presets + free text)
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.editor_field_schedule),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val dateLabel = draft.scheduledDateEpochDay?.let {
                            GermanDateFormat.formatFull(SimpleDate.fromEpochDay(it))
                        } ?: stringResource(R.string.editor_pick_date)

                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("editor_pick_date_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = stringResource(R.string.cd_date_picker),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = dateLabel)
                        }

                        if (draft.scheduledDateEpochDay != null) {
                            IconButton(
                                onClick = {
                                    onDraftChange(
                                        draft.copy(
                                            scheduledDateEpochDay = null,
                                            scheduledTimeText = ""
                                        )
                                    )
                                },
                                modifier = Modifier.testTag("editor_clear_date_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.editor_clear_date)
                                )
                            }
                        }
                    }

                    if (draft.scheduledDateEpochDay != null) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item(key = "time_icon") {
                                Icon(
                                    imageVector = Icons.Rounded.Schedule,
                                    contentDescription = stringResource(R.string.editor_field_time),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            items(TimePresets, key = { it }) { preset ->
                                FilterChip(
                                    selected = draft.scheduledTimeText == preset,
                                    onClick = {
                                        val next = if (draft.scheduledTimeText == preset) "" else preset
                                        onDraftChange(draft.copy(scheduledTimeText = next))
                                    },
                                    label = { Text(preset) },
                                    modifier = Modifier.testTag("editor_time_$preset")
                                )
                            }
                        }

                        OutlinedTextField(
                            value = draft.scheduledTimeText,
                            onValueChange = { input ->
                                if (input.length <= 10) {
                                    onDraftChange(draft.copy(scheduledTimeText = input))
                                }
                            },
                            label = { Text(stringResource(R.string.editor_field_time)) },
                            placeholder = { Text(stringResource(R.string.editor_time_custom_placeholder)) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("editor_custom_time_input")
                        )
                    }
                }

                // Script Notes & CTA
                OutlinedTextField(
                    value = draft.notes,
                    onValueChange = { onDraftChange(draft.copy(notes = it)) },
                    label = { Text(stringResource(R.string.editor_field_notes)) },
                    placeholder = { Text(stringResource(R.string.editor_field_notes_placeholder)) },
                    minLines = 3,
                    maxLines = 6,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("editor_notes_input")
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Bottom action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = CircleShape,
                    modifier = Modifier
                        .weight(0.4f)
                        .height(48.dp)
                        .testTag("editor_cancel_button")
                ) {
                    Text(stringResource(R.string.editor_cancel))
                }
                Button(
                    onClick = {
                        if (draft.title.isBlank()) {
                            showTitleError = true
                        } else {
                            onSave(draft)
                        }
                    },
                    shape = CircleShape,
                    modifier = Modifier
                        .weight(0.6f)
                        .height(48.dp)
                        .testTag("editor_save_button")
                ) {
                    Text(stringResource(R.string.editor_save))
                }
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = draft.scheduledDateEpochDay?.let {
            SimpleDate.fromEpochDay(it).toUtcMidnightMillis()
        } ?: SimpleDate.today().toUtcMidnightMillis()

        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val nextStatus = if (draft.status == ContentStatus.IDEA) {
                                ContentStatus.SCHEDULED
                            } else {
                                draft.status
                            }
                            onDraftChange(
                                draft.copy(
                                    scheduledDateEpochDay = SimpleDate.fromUtcMillis(selectedMillis).toEpochDay(),
                                    status = nextStatus
                                )
                            )
                        }
                        showDatePicker = false
                    },
                    modifier = Modifier.testTag("date_picker_confirm")
                ) {
                    Text(stringResource(R.string.goal_dialog_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.editor_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirm && isEditing) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(stringResource(R.string.editor_delete_confirm_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.editor_delete_confirm_body,
                        draft.title
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(draft.editingId)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    modifier = Modifier.testTag("editor_confirm_delete_button")
                ) {
                    Text(stringResource(R.string.editor_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.editor_cancel))
                }
            }
        )
    }
}
