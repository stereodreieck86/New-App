package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.ContentItem
import com.example.model.ContentPlatform
import com.example.model.ContentStatus
import com.example.model.HookStyle

@Entity(tableName = "content_items")
data class ContentItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val hook: String,
    val hookStyle: String,
    val notes: String,
    val platform: String,
    val status: String,
    val scheduledDateEpochDay: Long?,
    val scheduledTimeText: String,
    val isPinned: Boolean,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)

fun ContentItemEntity.toDomain(): ContentItem = ContentItem(
    id = id,
    title = title,
    hook = hook,
    hookStyle = HookStyle.fromKey(hookStyle),
    notes = notes,
    platform = ContentPlatform.fromKey(platform),
    status = ContentStatus.fromKey(status),
    scheduledDateEpochDay = scheduledDateEpochDay,
    scheduledTimeText = scheduledTimeText,
    isPinned = isPinned,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis
)

fun ContentItem.toEntity(): ContentItemEntity = ContentItemEntity(
    id = id,
    title = title,
    hook = hook,
    hookStyle = hookStyle.name,
    notes = notes,
    platform = platform.name,
    status = status.name,
    scheduledDateEpochDay = scheduledDateEpochDay,
    scheduledTimeText = scheduledTimeText,
    isPinned = isPinned,
    createdAtMillis = createdAtMillis,
    updatedAtMillis = updatedAtMillis
)
