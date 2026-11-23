package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentItemDao {
    @Query("SELECT * FROM content_items ORDER BY isPinned DESC, updatedAtMillis DESC")
    fun observeAll(): Flow<List<ContentItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ContentItemEntity): Long

    @Update
    suspend fun update(item: ContentItemEntity)

    @Query("DELETE FROM content_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE content_items SET status = :status, updatedAtMillis = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, updatedAt: Long)

    @Query("UPDATE content_items SET isPinned = :isPinned, updatedAtMillis = :updatedAt WHERE id = :id")
    suspend fun updatePinned(id: Long, isPinned: Boolean, updatedAt: Long)

    @Query(
        "UPDATE content_items SET scheduledDateEpochDay = :epochDay, status = :status, updatedAtMillis = :updatedAt WHERE id = :id"
    )
    suspend fun updateSchedule(id: Long, epochDay: Long, status: String, updatedAt: Long)
}
