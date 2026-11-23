package com.example

import com.example.model.ContentItem
import com.example.model.GermanDateFormat
import com.example.model.SimpleDate
import com.example.model.YearMonthKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.TimeZone

class ExampleUnitTest {

    @Test
    fun `simpleDate epochDay round-trip handles leap years accurately`() {
        val leapDay = SimpleDate(2024, 2, 29)
        val roundTripped = SimpleDate.fromEpochDay(leapDay.toEpochDay())
        assertEquals(leapDay, roundTripped)
        assertEquals(29, SimpleDate.daysInMonth(2024, 2))
        assertEquals(28, SimpleDate.daysInMonth(2026, 2))
    }

    @Test
    fun `simpleDate dayOfWeekMondayFirst returns Monday as 0 and Sunday as 6`() {
        // 2026-11-23 is Monday
        val monday = SimpleDate(2026, 11, 23)
        val sunday = SimpleDate(2026, 11, 29)
        assertEquals(0, monday.dayOfWeekMondayFirst())
        assertEquals(6, sunday.dayOfWeekMondayFirst())
    }

    @Test
    fun `fromLocalMillis respects local timezone across midnight`() {
        val berlin = TimeZone.getTimeZone("Europe/Berlin")
        val cal = Calendar.getInstance(berlin).apply {
            set(2026, Calendar.NOVEMBER, 23, 0, 30, 0) // Monday 00:30 CET = Sunday 23:30 UTC
            set(Calendar.MILLISECOND, 0)
        }
        val localDate = SimpleDate.fromLocalMillis(cal.timeInMillis, berlin)
        val utcDate = SimpleDate.fromUtcMillis(cal.timeInMillis)

        assertEquals(SimpleDate(2026, 11, 23), localDate)
        assertEquals(SimpleDate(2026, 11, 22), utcDate)
    }

    @Test
    fun `yearMonthKey navigation wraps across year boundaries`() {
        assertEquals(YearMonthKey(2025, 12), YearMonthKey(2026, 1).previous())
        assertEquals(YearMonthKey(2027, 1), YearMonthKey(2026, 12).next())
    }

    @Test
    fun `germanDateFormat formats relative and numeric dates`() {
        val today = SimpleDate(2026, 11, 23)
        assertEquals("Heute", GermanDateFormat.formatShort(today, today))
        assertEquals("Morgen", GermanDateFormat.formatShort(today.plusDays(1), today))
        assertEquals("Gestern", GermanDateFormat.formatShort(today.plusDays(-1), today))
        assertEquals("23.11.2026", GermanDateFormat.formatNumeric(today))
    }

    @Test
    fun `contentItem calculates hook word count and spoken duration`() {
        val item = ContentItem(
            title = "Video-Idee",
            hook = "Fast niemand kennt diesen einfachen Schnitt-Trick für mehr Watchtime."
        )
        assertTrue(item.hasHook)
        assertEquals(9, item.hookWordCount())
        assertEquals(4, item.estimatedHookSeconds())
    }
}
