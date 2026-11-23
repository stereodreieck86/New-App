package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ContentRepository
import com.example.data.CreatorDeskDatabase
import com.example.model.ContentItem
import com.example.model.ContentPlatform
import com.example.model.ContentStatus
import com.example.model.SimpleDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: CreatorDeskDatabase
    private lateinit var repository: ContentRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, CreatorDeskDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ContentRepository(database.contentItemDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `app name matches Creator Desk`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Creator Desk", appName)
    }

    @Test
    fun `editing an item preserves original createdAtMillis`() = runTest {
        val id = repository.save(
            ContentItem(
                title = "Erste Idee",
                hook = "Starker Einstieg",
                platform = ContentPlatform.YOUTUBE,
                status = ContentStatus.IDEA
            )
        )
        val inserted = repository.allItems.first().single { it.id == id }

        repository.save(
            inserted.copy(
                title = "Bearbeitete Idee",
                scheduledTimeText = "17:30"
            )
        )
        val updated = repository.allItems.first().single { it.id == id }

        assertEquals("Bearbeitete Idee", updated.title)
        assertEquals("17:30", updated.scheduledTimeText)
        assertEquals(inserted.createdAtMillis, updated.createdAtMillis)
    }

    @Test
    fun `publishing records publishedAtEpochDay and pinning keeps it unchanged`() = runTest {
        val id = repository.save(
            ContentItem(
                title = "Kurzvideo",
                platform = ContentPlatform.TIKTOK,
                status = ContentStatus.SCHEDULED
            )
        )
        val initial = repository.allItems.first().single { it.id == id }
        assertNull(initial.publishedAtEpochDay)

        repository.updateStatus(id, ContentStatus.PUBLISHED)
        val published = repository.allItems.first().single { it.id == id }
        assertNotNull(published.publishedAtEpochDay)
        assertEquals(SimpleDate.today().toEpochDay(), published.publishedAtEpochDay)

        repository.togglePinned(id, published.isPinned)
        val pinned = repository.allItems.first().single { it.id == id }
        assertEquals(true, pinned.isPinned)
        assertEquals(published.publishedAtEpochDay, pinned.publishedAtEpochDay)
    }
}
