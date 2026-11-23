package com.example.data

import com.example.model.ContentItem
import com.example.model.ContentStatus
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
        return if (item.id == 0L) {
            dao.insert(item.copy(createdAtMillis = now, updatedAtMillis = now).toEntity())
        } else {
            dao.update(item.copy(updatedAtMillis = now).toEntity())
            item.id
        }
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    suspend fun updateStatus(id: Long, status: ContentStatus) {
        dao.updateStatus(id = id, status = status.name, updatedAt = System.currentTimeMillis())
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
