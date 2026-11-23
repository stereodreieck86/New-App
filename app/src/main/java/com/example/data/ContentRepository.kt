package com.example.data

import com.example.model.ContentItem
import com.example.model.ContentStatus
import com.example.model.SimpleDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ContentRepository(
    private val dao: ContentItemDao
) {
    val allItems: Flow<List<ContentItem>> = dao.observeAll().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun save(item: ContentItem): Long {
        val now = System.currentTimeMillis()
        val resolvedPublishedEpochDay = when {
            item.status == ContentStatus.PUBLISHED ->
                item.publishedAtEpochDay ?: SimpleDate.today().toEpochDay()
            else -> null
        }

        return if (item.id == 0L) {
            dao.insert(
                item.copy(
                    publishedAtEpochDay = resolvedPublishedEpochDay,
                    createdAtMillis = now,
                    updatedAtMillis = now
                ).toEntity()
            )
        } else {
            dao.update(
                item.copy(
                    publishedAtEpochDay = resolvedPublishedEpochDay,
                    updatedAtMillis = now
                ).toEntity()
            )
            item.id
        }
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun updateStatus(
        id: Long,
        status: ContentStatus,
        existingPublishedEpochDay: Long? = null
    ) {
        val publishedDay = if (status == ContentStatus.PUBLISHED) {
            existingPublishedEpochDay ?: SimpleDate.today().toEpochDay()
        } else {
            null
        }
        dao.updateStatus(
            id = id,
            status = status.name,
            publishedAtEpochDay = publishedDay,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun togglePinned(id: Long, currentPinned: Boolean) {
        dao.updatePinned(
            id = id,
            isPinned = !currentPinned,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun scheduleItem(id: Long, epochDay: Long, currentStatus: ContentStatus) {
        val nextStatus = if (currentStatus == ContentStatus.PUBLISHED) {
            ContentStatus.PUBLISHED
        } else {
            ContentStatus.SCHEDULED
        }
        dao.updateSchedule(
            id = id,
            epochDay = epochDay,
            status = nextStatus.name,
            updatedAt = System.currentTimeMillis()
        )
    }
}
