package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.FormatQuote
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.ThemeMode
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.ContentEditorSheet
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HooksScreen
import com.example.ui.screens.IdeasScreen
import com.example.viewmodel.CreatorDeskUiState
import com.example.viewmodel.CreatorDeskViewModel
import com.example.viewmodel.CreatorTab
import kotlin.math.roundToInt

private data class NavDestination(
    val tab: CreatorTab,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

private val TopLevelDestinations = listOf(
    NavDestination(
        tab = CreatorTab.DASHBOARD,
        labelRes = R.string.nav_dashboard,
        selectedIcon = Icons.Rounded.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        testTag = "nav_tab_dashboard"
    ),
    NavDestination(
        tab = CreatorTab.IDEAS,
        labelRes = R.string.nav_ideas,
        selectedIcon = Icons.Rounded.Lightbulb,
        unselectedIcon = Icons.Outlined.Lightbulb,
        testTag = "nav_tab_ideas"
    ),
    NavDestination(
        tab = CreatorTab.HOOKS,
        labelRes = R.string.nav_hooks,
        selectedIcon = Icons.Rounded.FormatQuote,
        unselectedIcon = Icons.Outlined.FormatQuote,
        testTag = "nav_tab_hooks"
    ),
    NavDestination(
        tab = CreatorTab.CALENDAR,
        labelRes = R.string.nav_calendar,
        selectedIcon = Icons.Rounded.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth,
        testTag = "nav_tab_calendar"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorDeskApp(
    viewModel: CreatorDeskViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedSnackbarText = stringResource(R.string.editor_saved_snackbar)
    val deletedSnackbarText = stringResource(R.string.editor_deleted_snackbar)

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeSnackbar()
        }
    }

    // Back gesture from secondary tabs returns to Dashboard
    BackHandler(enabled = uiState.activeTab != CreatorTab.DASHBOARD) {
        viewModel.selectTab(CreatorTab.DASHBOARD)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 680.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                CreatorTopBar(
                    uiState = uiState,
                    onSearchClick = {
                        viewModel.selectTab(CreatorTab.IDEAS)
                    },
                    onCycleTheme = { viewModel.cycleThemeMode() }
                )
            },
            bottomBar = {
                if (!useNavigationRail) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 0.dp,
                        windowInsets = WindowInsets.navigationBars
                    ) {
                        TopLevelDestinations.forEach { dest ->
                            val selected = uiState.activeTab == dest.tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(dest.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        when (uiState.activeTab) {
                            CreatorTab.CALENDAR -> viewModel.openNewItemEditor(uiState.selectedDate)
                            CreatorTab.HOOKS -> viewModel.openNewHookEditor()
                            else -> viewModel.openNewItemEditor()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(R.string.action_new_idea)
                        )
                    },
                    text = {
                        Text(
                            text = when (uiState.activeTab) {
                                CreatorTab.HOOKS -> "Neuer Hook"
                                else -> stringResource(R.string.action_new_idea)
                            }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = CircleShape,
                    modifier = Modifier.testTag("fab_new_content")
                )
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useNavigationRail) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Spacer(modifier = Modifier.weight(0.1f))
                        TopLevelDestinations.forEach { dest ->
                            val selected = uiState.activeTab == dest.tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(dest.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    val contentMaxWidth = if (uiState.activeTab == CreatorTab.CALENDAR) 1080.dp else 760.dp

                    AnimatedContent(
                        targetState = uiState.activeTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        modifier = Modifier
                            .fillMaxHeight()
                            .widthIn(max = contentMaxWidth)
                            .fillMaxWidth(),
                        label = "tab_content"
                    ) { tab ->
                        when (tab) {
                            CreatorTab.DASHBOARD -> DashboardScreen(
                                uiState = uiState,
                                onCreateNewIdea = { viewModel.openNewItemEditor() },
                                onEditItem = { viewModel.openEditItemEditor(it) },
                                onAdvanceStatus = { viewModel.advanceStatus(it) },
                                onTogglePin = { viewModel.togglePin(it) },
                                onOpenStatusFilter = { viewModel.openIdeasWithStatusFilter(it) },
                                onOpenCalendar = { viewModel.selectTab(CreatorTab.CALENDAR) },
                                onOpenIdeas = { viewModel.selectTab(CreatorTab.IDEAS) },
                                onOpenGoalDialog = { viewModel.openGoalDialog() }
                            )

                            CreatorTab.IDEAS -> IdeasScreen(
                                uiState = uiState,
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onSelectPlatform = { viewModel.selectPlatformFilter(it) },
                                onSelectStatus = { viewModel.selectStatusFilter(it) },
                                onClearFilters = { viewModel.clearFilters() },
                                onCreateNewIdea = { viewModel.openNewItemEditor() },
                                onEditItem = { viewModel.openEditItemEditor(it) },
                                onAdvanceStatus = { viewModel.advanceStatus(it) },
                                onTogglePin = { viewModel.togglePin(it) }
                            )

                            CreatorTab.HOOKS -> HooksScreen(
                                uiState = uiState,
                                onUseTemplate = { viewModel.openNewHookEditor(it) },
                                onCreateHook = { viewModel.openNewHookEditor() },
                                onEditItem = { viewModel.openEditItemEditor(it) },
                                onHookCopied = { viewModel.showSnackbar(it) }
                            )

                            CreatorTab.CALENDAR -> CalendarScreen(
                                uiState = uiState,
                                onSelectDate = { viewModel.selectCalendarDate(it) },
                                onPreviousMonth = { viewModel.previousMonth() },
                                onNextMonth = { viewModel.nextMonth() },
                                onJumpToday = { viewModel.jumpCalendarToToday() },
                                onAddForSelectedDate = { viewModel.openNewItemEditor(uiState.selectedDate) },
                                onEditItem = { viewModel.openEditItemEditor(it) },
                                onAdvanceStatus = { viewModel.advanceStatus(it) },
                                onTogglePin = { viewModel.togglePin(it) },
                                onQuickSchedule = { item, date -> viewModel.quickScheduleItem(item, date) }
                            )
                        }
                    }
                }
            }
        }
    }

    val draft = uiState.editorDraft
    if (draft != null) {
        ContentEditorSheet(
            initialDraft = draft,
            onDismiss = { viewModel.closeEditor() },
            onSave = { updatedDraft ->
                viewModel.saveContentItem(updatedDraft, savedSnackbarText)
            },
            onDelete = { id ->
                viewModel.deleteContentItem(id, deletedSnackbarText)
            }
        )
    }

    if (uiState.showGoalDialog) {
        WeeklyGoalDialog(
            currentGoal = uiState.weeklyGoal,
            onDismiss = { viewModel.closeGoalDialog() },
            onSave = { viewModel.updateWeeklyGoal(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatorTopBar(
    uiState: CreatorDeskUiState,
    onSearchClick: () -> Unit,
    onCycleTheme: () -> Unit
) {
    val themeIcon = when (uiState.themeMode) {
        ThemeMode.SYSTEM -> Icons.Rounded.BrightnessAuto
        ThemeMode.LIGHT -> Icons.Rounded.LightMode
        ThemeMode.DARK -> Icons.Rounded.DarkMode
    }
    val themeLabel = when (uiState.themeMode) {
        ThemeMode.SYSTEM -> stringResource(R.string.theme_system)
        ThemeMode.LIGHT -> stringResource(R.string.theme_light)
        ThemeMode.DARK -> stringResource(R.string.theme_dark)
    }

    val screenTitle = when (uiState.activeTab) {
        CreatorTab.DASHBOARD -> stringResource(R.string.app_name)
        CreatorTab.IDEAS -> stringResource(R.string.nav_ideas)
        CreatorTab.HOOKS -> stringResource(R.string.nav_hooks)
        CreatorTab.CALENDAR -> stringResource(R.string.nav_calendar)
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = screenTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            if (uiState.activeTab != CreatorTab.IDEAS) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("topbar_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.action_search),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Surface(
                onClick = onCycleTheme,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .testTag("topbar_theme_toggle")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = themeIcon,
                        contentDescription = stringResource(R.string.action_theme_toggle),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = themeLabel,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun WeeklyGoalDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var sliderValue by remember(currentGoal) { mutableFloatStateOf(currentGoal.toFloat()) }
    val selectedGoal = sliderValue.roundToInt().coerceIn(1, 14)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.goal_dialog_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.goal_dialog_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.goal_dialog_value, selectedGoal),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 1f..14f,
                    steps = 12,
                    modifier = Modifier.testTag("weekly_goal_slider")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedGoal) },
                modifier = Modifier.testTag("weekly_goal_save_button")
            ) {
                Text(stringResource(R.string.goal_dialog_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.editor_cancel))
            }
        }
    )
}
