package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.ContentItem
import com.example.model.ContentPlatform
import com.example.model.ContentStatus
import com.example.ui.components.ContentItemCard
import com.example.ui.components.EmptyStateCard
import com.example.viewmodel.CreatorDeskUiState

@Composable
fun IdeasScreen(
    uiState: CreatorDeskUiState,
    onSearchQueryChange: (String) -> Unit,
    onSelectPlatform: (ContentPlatform?) -> Unit,
    onSelectStatus: (ContentStatus?) -> Unit,
    onClearFilters: () -> Unit,
    onCreateNewIdea: () -> Unit,
    onEditItem: (ContentItem) -> Unit,
    onAdvanceStatus: (ContentItem) -> Unit,
    onTogglePin: (ContentItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters = uiState.searchQuery.isNotBlank() ||
        uiState.selectedPlatformFilter != null ||
        uiState.selectedStatusFilter != null

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ideas_screen"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 104.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item(key = "ideas_search_box") {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("ideas_search_input"),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.action_search),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.testTag("ideas_search_clear")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.search_clear)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
        }

        item(key = "platform_filter_row") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item(key = "platform_all") {
                        FilterChip(
                            selected = uiState.selectedPlatformFilter == null,
                            onClick = { onSelectPlatform(null) },
                            label = { Text(stringResource(R.string.filter_all)) },
                            modifier = Modifier.testTag("filter_platform_all")
                        )
                    }
                    items(ContentPlatform.entries, key = { it.name }) { platform ->
                        val selected = uiState.selectedPlatformFilter == platform
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectPlatform(platform) },
                            leadingIcon = {
                                Icon(
                                    imageVector = platform.icon,
                                    contentDescription = null,
                                    tint = if (selected) {
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    } else {
                                        platform.accentColor
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(platform.displayName) },
                            modifier = Modifier.testTag("filter_platform_${platform.name.lowercase()}")
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ContentStatus.entries, key = { it.name }) { status ->
                        val selected = uiState.selectedStatusFilter == status
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectStatus(status) },
                            leadingIcon = {
                                Icon(
                                    imageVector = status.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(status.displayName) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.testTag("filter_status_${status.name.lowercase()}")
                        )
                    }
                }
            }
        }

        if (uiState.allItems.isEmpty()) {
            item(key = "ideas_empty") {
                EmptyStateCard(
                    icon = Icons.Rounded.Lightbulb,
                    title = stringResource(R.string.ideas_empty_title),
                    body = stringResource(R.string.ideas_empty_body),
                    ctaLabel = stringResource(R.string.action_new_idea),
                    onCtaClick = onCreateNewIdea,
                    ctaTestTag = "ideas_empty_cta",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else if (uiState.filteredItems.isEmpty()) {
            item(key = "ideas_no_results") {
                EmptyStateCard(
                    icon = Icons.Rounded.SearchOff,
                    title = stringResource(R.string.ideas_no_results_title),
                    body = stringResource(R.string.ideas_no_results_body),
                    ctaLabel = if (hasActiveFilters) stringResource(R.string.ideas_reset_filters) else null,
                    onCtaClick = if (hasActiveFilters) onClearFilters else null,
                    ctaTestTag = "ideas_reset_filters_button",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        } else {
            items(
                items = uiState.filteredItems,
                key = { it.id }
            ) { item ->
                ContentItemCard(
                    item = item,
                    onClick = { onEditItem(item) },
                    onAdvanceStatus = { onAdvanceStatus(item) },
                    onTogglePin = { onTogglePin(item) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
