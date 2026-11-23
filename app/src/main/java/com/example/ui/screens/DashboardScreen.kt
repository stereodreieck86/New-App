package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ContentItem
import com.example.model.ContentStatus
import com.example.model.GermanDateFormat
import com.example.model.SimpleDate
import com.example.ui.components.CircularProgressRing
import com.example.ui.components.ContentItemCard
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.PlatformBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.statusTint
import com.example.ui.theme.HeroCardBase
import com.example.ui.theme.HeroInkPrimary
import com.example.ui.theme.HeroInkSecondary
import com.example.ui.theme.StudioCoralDark
import com.example.ui.theme.StudioEmeraldDark
import com.example.viewmodel.CreatorDeskUiState

@Composable
fun DashboardScreen(
    uiState: CreatorDeskUiState,
    onCreateNewIdea: () -> Unit,
    onEditItem: (ContentItem) -> Unit,
    onAdvanceStatus: (ContentItem) -> Unit,
    onTogglePin: (ContentItem) -> Unit,
    onOpenStatusFilter: (ContentStatus) -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenIdeas: () -> Unit,
    onOpenGoalDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val metrics = uiState.metrics
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item(key = "hero_studio_card") {
            StudioHeroCard(
                publishedThisWeek = metrics.publishedThisWeek,
                weeklyGoal = metrics.weeklyGoal,
                overallProgress = metrics.overallProgress,
                onCreateNewIdea = onCreateNewIdea,
                onOpenGoalDialog = onOpenGoalDialog
            )
        }

        item(key = "kpi_bento_grid") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        label = stringResource(R.string.dashboard_kpi_total),
                        value = metrics.totalIdeas.toString(),
                        icon = Icons.Rounded.Lightbulb,
                        accentColor = MaterialTheme.colorScheme.primary,
                        onClick = onOpenIdeas,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_total_ideas")
                    )
                    KpiCard(
                        label = stringResource(R.string.dashboard_kpi_hooks),
                        value = metrics.hooksReady.toString(),
                        icon = Icons.Rounded.FormatQuote,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        onClick = { onOpenStatusFilter(ContentStatus.SCRIPTING) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_hooks_ready")
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        label = stringResource(R.string.dashboard_kpi_scheduled),
                        value = metrics.scheduledCount.toString(),
                        icon = Icons.Rounded.EventAvailable,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        onClick = onOpenCalendar,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_scheduled")
                    )
                    KpiCard(
                        label = stringResource(R.string.dashboard_kpi_published),
                        value = metrics.publishedCount.toString(),
                        icon = Icons.Rounded.CheckCircle,
                        accentColor = MaterialTheme.colorScheme.tertiary,
                        onClick = { onOpenStatusFilter(ContentStatus.PUBLISHED) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_published")
                    )
                }
            }
        }

        item(key = "pipeline_progress") {
            PipelineProgressCard(
                countsByStatus = metrics.countsByStatus,
                total = metrics.totalIdeas,
                onStatusSelected = onOpenStatusFilter
            )
        }

        if (uiState.allItems.isEmpty()) {
            item(key = "dashboard_empty_state") {
                EmptyStateCard(
                    icon = Icons.Rounded.AutoAwesome,
                    title = stringResource(R.string.dashboard_empty_title),
                    body = stringResource(R.string.dashboard_empty_body),
                    ctaLabel = stringResource(R.string.dashboard_empty_cta),
                    onCtaClick = onCreateNewIdea,
                    ctaTestTag = "dashboard_empty_create_button"
                )
            }
        } else {
            item(key = "upcoming_header") {
                SectionHeader(
                    title = stringResource(R.string.dashboard_upcoming_title),
                    actionLabel = stringResource(R.string.nav_calendar),
                    onActionClick = onOpenCalendar
                )
            }

            if (uiState.upcomingItems.isEmpty()) {
                item(key = "upcoming_empty") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenCalendar),
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(R.string.dashboard_upcoming_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(
                    items = uiState.upcomingItems.take(4),
                    key = { "upcoming_${it.id}" }
                ) { item ->
                    UpcomingScheduleRow(
                        item = item,
                        onClick = { onEditItem(item) }
                    )
                }
            }

            if (uiState.recentItems.isNotEmpty()) {
                item(key = "recent_header") {
                    SectionHeader(
                        title = stringResource(R.string.dashboard_recent_title),
                        actionLabel = stringResource(R.string.nav_ideas),
                        onActionClick = onOpenIdeas
                    )
                }

                items(
                    items = uiState.recentItems,
                    key = { "recent_${it.id}" }
                ) { item ->
                    ContentItemCard(
                        item = item,
                        onClick = { onEditItem(item) },
                        onAdvanceStatus = { onAdvanceStatus(item) },
                        onTogglePin = { onTogglePin(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StudioHeroCard(
    publishedThisWeek: Int,
    weeklyGoal: Int,
    overallProgress: Float,
    onCreateNewIdea: () -> Unit,
    onOpenGoalDialog: () -> Unit
) {
    val weekProgress = if (weeklyGoal > 0) {
        (publishedThisWeek.toFloat() / weeklyGoal.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("studio_hero_card"),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = HeroCardBase)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_studio_banner_1795443049852),
                contentDescription = stringResource(R.string.cd_studio_banner),
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                HeroCardBase.copy(alpha = 0.94f),
                                HeroCardBase.copy(alpha = 0.78f),
                                HeroCardBase.copy(alpha = 0.56f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.dashboard_eyebrow),
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioCoralDark
                        )
                        Text(
                            text = stringResource(R.string.dashboard_hero_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = HeroInkPrimary
                        )
                        Text(
                            text = stringResource(
                                R.string.dashboard_hero_subtitle,
                                publishedThisWeek,
                                weeklyGoal
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = HeroInkSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    CircularProgressRing(
                        progress = if (publishedThisWeek > 0) weekProgress else overallProgress,
                        size = 82.dp,
                        strokeWidth = 8.dp,
                        trackColor = Color.White.copy(alpha = 0.16f),
                        indicatorColor = if (weekProgress >= 1f) StudioEmeraldDark else StudioCoralDark,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onOpenGoalDialog)
                            .testTag("weekly_goal_ring")
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$publishedThisWeek/$weeklyGoal",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = HeroInkPrimary
                            )
                            Text(
                                text = "Woche",
                                style = MaterialTheme.typography.labelSmall,
                                color = HeroInkSecondary
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onCreateNewIdea,
                        modifier = Modifier.testTag("hero_new_idea_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioCoralDark,
                            contentColor = Color(0xFF1A0603)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.action_new_idea),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.12f),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(onClick = onOpenGoalDialog)
                            .testTag("hero_adjust_goal_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Tune,
                                contentDescription = stringResource(R.string.dashboard_goal_adjust),
                                tint = HeroInkPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Ziel",
                                style = MaterialTheme.typography.labelLarge,
                                color = HeroInkPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    label: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PipelineProgressCard(
    countsByStatus: Map<ContentStatus, Int>,
    total: Int,
    onStatusSelected: (ContentStatus) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = stringResource(R.string.dashboard_pipeline_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Segmented proportional bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (total > 0) {
                    ContentStatus.entries.forEach { status ->
                        val count = countsByStatus[status] ?: 0
                        if (count > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(count.toFloat())
                                    .height(10.dp)
                                    .background(statusTint(status))
                            )
                        }
                    }
                }
            }

            // Interactive stage pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ContentStatus.entries.forEach { status ->
                    val count = countsByStatus[status] ?: 0
                    val tint = statusTint(status)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.small)
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                            .clickable { onStatusSelected(status) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                            .testTag("pipeline_stage_${status.name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (count > 0) tint else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = status.shortLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingScheduleRow(
    item: ContentItem,
    onClick: () -> Unit
) {
    val date = item.scheduledDateEpochDay?.let { SimpleDate.fromEpochDay(it) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("upcoming_row_${item.id}"),
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
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (date != null) {
                Column(
                    modifier = Modifier
                        .size(width = 56.dp, height = 56.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = date.day.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = GermanDateFormat.monthName(date.month).take(3).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlatformBadge(platform = item.platform, compact = true)
                    if (date != null) {
                        val timePart = if (item.scheduledTimeText.isNotBlank()) " · ${item.scheduledTimeText}" else ""
                        Text(
                            text = GermanDateFormat.formatShort(date) + timePart,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
