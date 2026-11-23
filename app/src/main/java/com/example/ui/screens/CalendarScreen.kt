package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ContentItem
import com.example.model.GermanDateFormat
import com.example.model.SimpleDate
import com.example.model.YearMonthKey
import com.example.ui.components.ContentItemCard
import com.example.ui.components.PlatformBadge
import com.example.ui.components.SectionHeader
import com.example.viewmodel.CreatorDeskUiState

@Composable
fun CalendarScreen(
    uiState: CreatorDeskUiState,
    onSelectDate: (SimpleDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToday: () -> Unit,
    onAddForSelectedDate: () -> Unit,
    onEditItem: (ContentItem) -> Unit,
    onAdvanceStatus: (ContentItem) -> Unit,
    onTogglePin: (ContentItem) -> Unit,
    onQuickSchedule: (ContentItem, SimpleDate) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("calendar_screen")
    ) {
        val isWide = maxWidth >= 780.dp

        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MonthCalendarCard(
                    month = uiState.calendarMonth,
                    selectedDate = uiState.selectedDate,
                    itemsByEpochDay = uiState.itemsByEpochDay,
                    onSelectDate = onSelectDate,
                    onPreviousMonth = onPreviousMonth,
                    onNextMonth = onNextMonth,
                    onJumpToday = onJumpToday,
                    modifier = Modifier.weight(1.05f)
                )

                DayScheduleList(
                    selectedDate = uiState.selectedDate,
                    dayItems = uiState.selectedDayItems,
                    unscheduledItems = uiState.unscheduledItems,
                    onAddForSelectedDate = onAddForSelectedDate,
                    onEditItem = onEditItem,
                    onAdvanceStatus = onAdvanceStatus,
                    onTogglePin = onTogglePin,
                    onQuickSchedule = onQuickSchedule,
                    contentPadding = PaddingValues(bottom = 104.dp),
                    modifier = Modifier.weight(0.95f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 104.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item(key = "month_calendar") {
                    MonthCalendarCard(
                        month = uiState.calendarMonth,
                        selectedDate = uiState.selectedDate,
                        itemsByEpochDay = uiState.itemsByEpochDay,
                        onSelectDate = onSelectDate,
                        onPreviousMonth = onPreviousMonth,
                        onNextMonth = onNextMonth,
                        onJumpToday = onJumpToday
                    )
                }

                item(key = "selected_day_header") {
                    SectionHeader(
                        title = stringResource(
                            R.string.calendar_day_schedule_title,
                            GermanDateFormat.formatShort(uiState.selectedDate)
                        )
                    )
                }

                if (uiState.selectedDayItems.isEmpty()) {
                    item(key = "selected_day_empty") {
                        DayEmptyCard(
                            selectedDate = uiState.selectedDate,
                            onAddForSelectedDate = onAddForSelectedDate
                        )
                    }
                } else {
                    items(
                        items = uiState.selectedDayItems,
                        key = { "day_item_${it.id}" }
                    ) { item ->
                        ContentItemCard(
                            item = item,
                            onClick = { onEditItem(item) },
                            onAdvanceStatus = { onAdvanceStatus(item) },
                            onTogglePin = { onTogglePin(item) }
                        )
                    }
                    item(key = "add_another_for_day") {
                        FilledTonalButton(
                            onClick = onAddForSelectedDate,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calendar_add_more_button"),
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.calendar_add_for_day))
                        }
                    }
                }

                if (uiState.unscheduledItems.isNotEmpty()) {
                    item(key = "unscheduled_header") {
                        SectionHeader(
                            title = stringResource(
                                R.string.calendar_unscheduled_title,
                                uiState.unscheduledItems.size
                            )
                        )
                    }
                    items(
                        items = uiState.unscheduledItems.take(6),
                        key = { "unscheduled_${it.id}" }
                    ) { item ->
                        UnscheduledQuickAssignCard(
                            item = item,
                            targetDate = uiState.selectedDate,
                            onEdit = { onEditItem(item) },
                            onAssign = { onQuickSchedule(item, uiState.selectedDate) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayScheduleList(
    selectedDate: SimpleDate,
    dayItems: List<ContentItem>,
    unscheduledItems: List<ContentItem>,
    onAddForSelectedDate: () -> Unit,
    onEditItem: (ContentItem) -> Unit,
    onAdvanceStatus: (ContentItem) -> Unit,
    onTogglePin: (ContentItem) -> Unit,
    onQuickSchedule: (ContentItem, SimpleDate) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "wide_day_header") {
            SectionHeader(
                title = GermanDateFormat.formatFull(selectedDate)
            )
        }
        if (dayItems.isEmpty()) {
            item(key = "wide_day_empty") {
                DayEmptyCard(
                    selectedDate = selectedDate,
                    onAddForSelectedDate = onAddForSelectedDate
                )
            }
        } else {
            items(dayItems, key = { "wide_day_${it.id}" }) { item ->
                ContentItemCard(
                    item = item,
                    onClick = { onEditItem(item) },
                    onAdvanceStatus = { onAdvanceStatus(item) },
                    onTogglePin = { onTogglePin(item) }
                )
            }
        }
        if (unscheduledItems.isNotEmpty()) {
            item(key = "wide_unscheduled_header") {
                SectionHeader(
                    title = stringResource(R.string.calendar_unscheduled_title, unscheduledItems.size)
                )
            }
            items(unscheduledItems.take(6), key = { "wide_unscheduled_${it.id}" }) { item ->
                UnscheduledQuickAssignCard(
                    item = item,
                    targetDate = selectedDate,
                    onEdit = { onEditItem(item) },
                    onAssign = { onQuickSchedule(item, selectedDate) }
                )
            }
        }
    }
}

@Composable
private fun MonthCalendarCard(
    month: YearMonthKey,
    selectedDate: SimpleDate,
    itemsByEpochDay: Map<Long, List<ContentItem>>,
    onSelectDate: (SimpleDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onJumpToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = SimpleDate.today()
    val firstDay = month.firstDay()
    val leadingBlanks = firstDay.dayOfWeekMondayFirst() // 0..6
    val totalDays = month.daysInMonth()
    val totalCells = leadingBlanks + totalDays
    val rowCount = (totalCells + 6) / 7

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = month.formattedGerman(),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onJumpToday,
                        modifier = Modifier.testTag("calendar_today_button")
                    ) {
                        Text(text = stringResource(R.string.calendar_today))
                    }
                    IconButton(
                        onClick = onPreviousMonth,
                        modifier = Modifier.testTag("calendar_prev_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.calendar_prev_month)
                        )
                    }
                    IconButton(
                        onClick = onNextMonth,
                        modifier = Modifier.testTag("calendar_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.calendar_next_month)
                        )
                    }
                }
            }

            // Weekday header row (Mo..So)
            Row(modifier = Modifier.fillMaxWidth()) {
                GermanDateFormat.weekdayHeaders.forEach { dayName ->
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Calendar Grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                for (rowIndex in 0 until rowCount) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (colIndex in 0 until 7) {
                            val cellIndex = rowIndex * 7 + colIndex
                            val dayNumber = cellIndex - leadingBlanks + 1
                            if (dayNumber in 1..totalDays) {
                                val date = SimpleDate(month.year, month.month, dayNumber)
                                val epochDay = date.toEpochDay()
                                val dayItems = itemsByEpochDay[epochDay].orEmpty()
                                val isSelected = date == selectedDate
                                val isToday = date == today

                                CalendarDayCell(
                                    dayNumber = dayNumber,
                                    isSelected = isSelected,
                                    isToday = isToday,
                                    dotColors = dayItems.take(3).map { it.platform.accentColor },
                                    onClick = { onSelectDate(date) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("calendar_day_${date.year}_${date.month}_${date.day}")
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    dayNumber: Int,
    isSelected: Boolean,
    isToday: Boolean,
    dotColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNumber.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(6.dp)
            ) {
                dotColors.forEach { dotColor ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else dotColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayEmptyCard(
    selectedDate: SimpleDate,
    onAddForSelectedDate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = GermanDateFormat.formatFull(selectedDate),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.calendar_day_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onAddForSelectedDate,
                modifier = Modifier.testTag("calendar_plan_day_button"),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.calendar_add_for_day))
            }
        }
    }
}

@Composable
private fun UnscheduledQuickAssignCard(
    item: ContentItem,
    targetDate: SimpleDate,
    onEdit: () -> Unit,
    onAssign: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEdit),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                PlatformBadge(platform = item.platform, compact = true)
            }

            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onAssign)
                    .testTag("quick_schedule_${item.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EventAvailable,
                        contentDescription = stringResource(R.string.calendar_schedule_here),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = GermanDateFormat.formatShort(targetDate),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
