package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ContentRepository
import com.example.data.ThemePreferencesRepository
import com.example.data.UserPreferences
import com.example.model.ContentItem
import com.example.model.ContentPlatform
import com.example.model.ContentStatus
import com.example.model.GermanDateFormat
import com.example.model.HookStyle
import com.example.model.HookTemplate
import com.example.model.SimpleDate
import com.example.model.ThemeMode
import com.example.model.YearMonthKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class CreatorTab {
    DASHBOARD,
    IDEAS,
    HOOKS,
    CALENDAR
}

data class EditorDraft(
    val editingId: Long = 0L,
    val title: String = "",
    val hook: String = "",
    val hookStyle: HookStyle = HookStyle.CURIOSITY,
    val notes: String = "",
    val platform: ContentPlatform = ContentPlatform.YOUTUBE,
    val status: ContentStatus = ContentStatus.IDEA,
    val scheduledDateEpochDay: Long? = null,
    val scheduledTimeText: String = "",
    val isPinned: Boolean = false
)

data class DashboardMetrics(
    val totalIdeas: Int = 0,
    val hooksReady: Int = 0,
    val scheduledCount: Int = 0,
    val publishedCount: Int = 0,
    val publishedThisWeek: Int = 0,
    val weeklyGoal: Int = 3,
    val overallProgress: Float = 0f,
    val countsByStatus: Map<ContentStatus, Int> = emptyMap()
)

data class CreatorDeskUiState(
    val activeTab: CreatorTab = CreatorTab.DASHBOARD,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val weeklyGoal: Int = 3,
    val allItems: List<ContentItem> = emptyList(),
    val filteredItems: List<ContentItem> = emptyList(),
    val hookItems: List<ContentItem> = emptyList(),
    val upcomingItems: List<ContentItem> = emptyList(),
    val recentItems: List<ContentItem> = emptyList(),
    val unscheduledItems: List<ContentItem> = emptyList(),
    val itemsByEpochDay: Map<Long, List<ContentItem>> = emptyMap(),
    val selectedDayItems: List<ContentItem> = emptyList(),
    val metrics: DashboardMetrics = DashboardMetrics(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedPlatformFilter: ContentPlatform? = null,
    val selectedStatusFilter: ContentStatus? = null,
    val calendarMonth: YearMonthKey = SimpleDate.today().let { YearMonthKey(it.year, it.month) },
    val selectedDate: SimpleDate = SimpleDate.today(),
    val editorDraft: EditorDraft? = null,
    val showGoalDialog: Boolean = false,
    val snackbarMessage: String? = null
)

private data class LocalUiFilterState(
    val activeTab: CreatorTab = CreatorTab.DASHBOARD,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedPlatformFilter: ContentPlatform? = null,
    val selectedStatusFilter: ContentStatus? = null,
    val calendarMonth: YearMonthKey = SimpleDate.today().let { YearMonthKey(it.year, it.month) },
    val selectedDate: SimpleDate = SimpleDate.today(),
    val editorDraft: EditorDraft? = null,
    val showGoalDialog: Boolean = false,
    val snackbarMessage: String? = null
)

class CreatorDeskViewModel(
    private val contentRepository: ContentRepository,
    private val preferencesRepository: ThemePreferencesRepository
) : ViewModel() {

    private val localState = MutableStateFlow(LocalUiFilterState())

    val uiState: StateFlow<CreatorDeskUiState> = combine(
        contentRepository.allItems,
        preferencesRepository.preferencesFlow,
        localState
    ) { items, prefs, local ->
        buildUiState(items, prefs, local)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = CreatorDeskUiState()
    )

    private fun buildUiState(
        items: List<ContentItem>,
        prefs: UserPreferences,
        local: LocalUiFilterState
    ): CreatorDeskUiState {
        val today = SimpleDate.today()
        val todayEpoch = today.toEpochDay()
        val weekRange = GermanDateFormat.currentWeekRange(today)

        val query = local.searchQuery.trim().lowercase()
        val filtered = items.filter { item ->
            val matchesQuery = query.isEmpty() ||
                item.title.lowercase().contains(query) ||
                item.hook.lowercase().contains(query) ||
                item.notes.lowercase().contains(query) ||
                item.platform.displayName.lowercase().contains(query) ||
                item.status.displayName.lowercase().contains(query)
            val matchesPlatform = local.selectedPlatformFilter == null ||
                item.platform == local.selectedPlatformFilter
            val matchesStatus = local.selectedStatusFilter == null ||
                item.status == local.selectedStatusFilter
            matchesQuery && matchesPlatform && matchesStatus
        }

        val hookItems = items.filter { it.hasHook && (query.isEmpty() ||
            it.hook.lowercase().contains(query) ||
            it.title.lowercase().contains(query) ||
            it.platform.displayName.lowercase().contains(query))
        }

        val upcoming = items
            .filter { item ->
                val day = item.scheduledDateEpochDay
                day != null && day >= todayEpoch && item.status != ContentStatus.PUBLISHED
            }
            .sortedWith(compareBy<ContentItem> { it.scheduledDateEpochDay }.thenBy { it.scheduledTimeText })

        val recent = items
            .filter { it.status != ContentStatus.PUBLISHED }
            .take(5)

        val unscheduled = items
            .filter { it.scheduledDateEpochDay == null && it.status != ContentStatus.PUBLISHED }

        val byDay = items
            .filter { it.scheduledDateEpochDay != null }
            .groupBy { it.scheduledDateEpochDay!! }
            .mapValues { (_, list) -> list.sortedBy { it.scheduledTimeText } }

        val selectedDayList = byDay[local.selectedDate.toEpochDay()].orEmpty()

        val countsByStatus = ContentStatus.entries.associateWith { status ->
            items.count { it.status == status }
        }

        val publishedCount = countsByStatus[ContentStatus.PUBLISHED] ?: 0
        val publishedThisWeek = items.count { item ->
            if (item.status != ContentStatus.PUBLISHED) return@count false
            val day = item.scheduledDateEpochDay ?: SimpleDate.fromUtcMillis(item.updatedAtMillis).toEpochDay()
            day in weekRange
        }

        val overallProgress = if (items.isEmpty()) {
            0f
        } else {
            items.map { it.status.progressWeight }.average().toFloat()
        }

        val metrics = DashboardMetrics(
            totalIdeas = items.size,
            hooksReady = items.count { it.hasHook },
            scheduledCount = items.count { it.scheduledDateEpochDay != null && it.status != ContentStatus.PUBLISHED },
            publishedCount = publishedCount,
            publishedThisWeek = publishedThisWeek,
            weeklyGoal = prefs.weeklyGoal,
            overallProgress = overallProgress,
            countsByStatus = countsByStatus
        )

        return CreatorDeskUiState(
            activeTab = local.activeTab,
            themeMode = prefs.themeMode,
            weeklyGoal = prefs.weeklyGoal,
            allItems = items,
            filteredItems = filtered,
            hookItems = hookItems,
            upcomingItems = upcoming,
            recentItems = recent,
            unscheduledItems = unscheduled,
            itemsByEpochDay = byDay,
            selectedDayItems = selectedDayList,
            metrics = metrics,
            searchQuery = local.searchQuery,
            isSearchActive = local.isSearchActive,
            selectedPlatformFilter = local.selectedPlatformFilter,
            selectedStatusFilter = local.selectedStatusFilter,
            calendarMonth = local.calendarMonth,
            selectedDate = local.selectedDate,
            editorDraft = local.editorDraft,
            showGoalDialog = local.showGoalDialog,
            snackbarMessage = local.snackbarMessage
        )
    }

    fun selectTab(tab: CreatorTab) {
        localState.update { it.copy(activeTab = tab) }
    }

    fun openIdeasWithStatusFilter(status: ContentStatus) {
        localState.update {
            it.copy(
                activeTab = CreatorTab.IDEAS,
                selectedStatusFilter = status,
                selectedPlatformFilter = null
            )
        }
    }

    fun toggleSearchBar() {
        localState.update { current ->
            val opening = !current.isSearchActive
            current.copy(
                isSearchActive = opening,
                searchQuery = if (!opening) "" else current.searchQuery,
                activeTab = if (opening && current.activeTab == CreatorTab.DASHBOARD) {
                    CreatorTab.IDEAS
                } else {
                    current.activeTab
                }
            )
        }
    }

    fun updateSearchQuery(query: String) {
        localState.update { it.copy(searchQuery = query) }
    }

    fun selectPlatformFilter(platform: ContentPlatform?) {
        localState.update { current ->
            val next = if (current.selectedPlatformFilter == platform) null else platform
            current.copy(selectedPlatformFilter = next)
        }
    }

    fun selectStatusFilter(status: ContentStatus?) {
        localState.update { current ->
            val next = if (current.selectedStatusFilter == status) null else status
            current.copy(selectedStatusFilter = next)
        }
    }

    fun clearFilters() {
        localState.update {
            it.copy(
                searchQuery = "",
                selectedPlatformFilter = null,
                selectedStatusFilter = null
            )
        }
    }

    fun cycleThemeMode() {
        val next = uiState.value.themeMode.next()
        viewModelScope.launch {
            preferencesRepository.setThemeMode(next)
        }
    }

    fun openGoalDialog() {
        localState.update { it.copy(showGoalDialog = true) }
    }

    fun closeGoalDialog() {
        localState.update { it.copy(showGoalDialog = false) }
    }

    fun updateWeeklyGoal(goal: Int) {
        viewModelScope.launch {
            preferencesRepository.setWeeklyGoal(goal)
            localState.update { it.copy(showGoalDialog = false) }
        }
    }

    fun selectCalendarDate(date: SimpleDate) {
        localState.update {
            it.copy(
                selectedDate = date,
                calendarMonth = YearMonthKey(date.year, date.month)
            )
        }
    }

    fun previousMonth() {
        localState.update { it.copy(calendarMonth = it.calendarMonth.previous()) }
    }

    fun nextMonth() {
        localState.update { it.copy(calendarMonth = it.calendarMonth.next()) }
    }

    fun jumpCalendarToToday() {
        val today = SimpleDate.today()
        localState.update {
            it.copy(
                selectedDate = today,
                calendarMonth = YearMonthKey(today.year, today.month)
            )
        }
    }

    fun openNewItemEditor(preselectedDate: SimpleDate? = null) {
        localState.update {
            it.copy(
                editorDraft = EditorDraft(
                    scheduledDateEpochDay = preselectedDate?.toEpochDay(),
                    status = if (preselectedDate != null) ContentStatus.SCHEDULED else ContentStatus.IDEA
                )
            )
        }
    }

    fun openNewHookEditor(template: HookTemplate? = null) {
        localState.update {
            it.copy(
                editorDraft = EditorDraft(
                    hook = template?.formula.orEmpty(),
                    hookStyle = template?.style ?: HookStyle.CURIOSITY,
                    status = ContentStatus.SCRIPTING
                )
            )
        }
    }

    fun openEditItemEditor(item: ContentItem) {
        localState.update {
            it.copy(
                editorDraft = EditorDraft(
                    editingId = item.id,
                    title = item.title,
                    hook = item.hook,
                    hookStyle = item.hookStyle,
                    notes = item.notes,
                    platform = item.platform,
                    status = item.status,
                    scheduledDateEpochDay = item.scheduledDateEpochDay,
                    scheduledTimeText = item.scheduledTimeText,
                    isPinned = item.isPinned
                )
            )
        }
    }

    fun closeEditor() {
        localState.update { it.copy(editorDraft = null) }
    }

    fun saveContentItem(draft: EditorDraft, savedMessage: String) {
        val trimmedTitle = draft.title.trim()
        if (trimmedTitle.isEmpty()) return

        val resolvedStatus = if (
            draft.scheduledDateEpochDay != null &&
            draft.status == ContentStatus.IDEA
        ) {
            ContentStatus.SCHEDULED
        } else if (draft.hook.isNotBlank() && draft.status == ContentStatus.IDEA && draft.editingId == 0L) {
            ContentStatus.SCRIPTING
        } else {
            draft.status
        }

        val item = ContentItem(
            id = draft.editingId,
            title = trimmedTitle,
            hook = draft.hook.trim(),
            hookStyle = draft.hookStyle,
            notes = draft.notes.trim(),
            platform = draft.platform,
            status = resolvedStatus,
            scheduledDateEpochDay = draft.scheduledDateEpochDay,
            scheduledTimeText = draft.scheduledTimeText.trim(),
            isPinned = draft.isPinned
        )

        viewModelScope.launch {
            contentRepository.save(item)
            localState.update {
                it.copy(
                    editorDraft = null,
                    snackbarMessage = savedMessage
                )
            }
        }
    }

    fun deleteContentItem(id: Long, deletedMessage: String) {
        viewModelScope.launch {
            contentRepository.deleteById(id)
            localState.update {
                it.copy(
                    editorDraft = null,
                    snackbarMessage = deletedMessage
                )
            }
        }
    }

    fun advanceStatus(item: ContentItem) {
        val next = item.status.next() ?: return
        viewModelScope.launch {
            contentRepository.updateStatus(item.id, next)
        }
    }

    fun togglePin(item: ContentItem) {
        viewModelScope.launch {
            contentRepository.togglePinned(item.id, item.isPinned)
        }
    }

    fun quickScheduleItem(item: ContentItem, date: SimpleDate) {
        viewModelScope.launch {
            contentRepository.scheduleItem(item.id, date.toEpochDay(), item.status)
        }
    }

    fun showSnackbar(message: String) {
        localState.update { it.copy(snackbarMessage = message) }
    }

    fun consumeSnackbar() {
        localState.update { it.copy(snackbarMessage = null) }
    }

    companion object {
        fun factory(
            contentRepository: ContentRepository,
            preferencesRepository: ThemePreferencesRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreatorDeskViewModel(
                    contentRepository = contentRepository,
                    preferencesRepository = preferencesRepository
                ) as T
            }
        }
    }
}
