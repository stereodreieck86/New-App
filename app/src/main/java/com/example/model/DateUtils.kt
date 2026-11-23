package com.example.model

import java.util.Calendar
import java.util.TimeZone

/**
 * Lightweight, API-24+ safe calendar date value object using civil epoch-day math
 * (Howard Hinnant's algorithm) so calendar matching is completely timezone-independent.
 */
data class SimpleDate(
    val year: Int,
    val month: Int, // 1..12
    val day: Int    // 1..31
) : Comparable<SimpleDate> {

    fun toEpochDay(): Long {
        val y = if (month <= 2) year - 1 else year
        val era = if (y >= 0) y / 400 else (y - 399) / 400
        val yoe = y - era * 400
        val m = month
        val doy = (153 * (m + if (m > 2) -3 else 9) + 2) / 5 + day - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097L + doe.toLong() - 719468L
    }

    fun toUtcMidnightMillis(): Long = toEpochDay() * MILLIS_PER_DAY

    /** Returns 0 for Monday .. 6 for Sunday. */
    fun dayOfWeekMondayFirst(): Int {
        // 1970-01-01 (epochDay 0) was Thursday -> index 3 in Monday-first week
        return (((toEpochDay() + 3) % 7 + 7) % 7).toInt()
    }

    fun plusDays(days: Long): SimpleDate = fromEpochDay(toEpochDay() + days)

    override fun compareTo(other: SimpleDate): Int =
        toEpochDay().compareTo(other.toEpochDay())

    companion object {
        private const val MILLIS_PER_DAY = 86_400_000L

        fun today(): SimpleDate = fromLocalMillis(System.currentTimeMillis())

        fun fromLocalMillis(
            millis: Long,
            timeZone: TimeZone = TimeZone.getDefault()
        ): SimpleDate {
            val cal = Calendar.getInstance(timeZone)
            cal.timeInMillis = millis
            return SimpleDate(
                year = cal.get(Calendar.YEAR),
                month = cal.get(Calendar.MONTH) + 1,
                day = cal.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun fromEpochDay(epochDay: Long): SimpleDate {
            val z = epochDay + 719468L
            val era = if (z >= 0) z / 146097L else (z - 146096L) / 146097L
            val doe = (z - era * 146097L).toInt()
            val yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365
            var y = yoe + (era * 400L).toInt()
            val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
            val mp = (5 * doy + 2) / 153
            val d = doy - (153 * mp + 2) / 5 + 1
            val m = mp + if (mp < 10) 3 else -9
            if (m <= 2) y += 1
            return SimpleDate(year = y, month = m, day = d)
        }

        fun fromUtcMillis(utcMillis: Long): SimpleDate =
            fromLocalMillis(utcMillis, TimeZone.getTimeZone("UTC"))

        fun daysInMonth(year: Int, month: Int): Int = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }

        private fun isLeapYear(year: Int): Boolean =
            (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}

data class YearMonthKey(
    val year: Int,
    val month: Int // 1..12
) {
    fun previous(): YearMonthKey =
        if (month == 1) YearMonthKey(year - 1, 12) else YearMonthKey(year, month - 1)

    fun next(): YearMonthKey =
        if (month == 12) YearMonthKey(year + 1, 1) else YearMonthKey(year, month + 1)

    fun firstDay(): SimpleDate = SimpleDate(year, month, 1)

    fun daysInMonth(): Int = SimpleDate.daysInMonth(year, month)

    fun formattedGerman(): String = "${GermanDateFormat.monthName(month)} $year"
}

object GermanDateFormat {
    val weekdayHeaders = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")

    private val fullMonths = listOf(
        "Januar", "Februar", "März", "April", "Mai", "Juni",
        "Juli", "August", "September", "Oktober", "November", "Dezember"
    )

    private val shortMonths = listOf(
        "Jan.", "Feb.", "März", "Apr.", "Mai", "Juni",
        "Juli", "Aug.", "Sep.", "Okt.", "Nov.", "Dez."
    )

    private val shortWeekdays = listOf("Mo", "Di", "Mi", "Do", "Fr", "Sa", "So")

    fun monthName(month: Int): String = fullMonths[(month - 1).coerceIn(0, 11)]

    fun formatShort(date: SimpleDate, today: SimpleDate = SimpleDate.today()): String {
        val diff = date.toEpochDay() - today.toEpochDay()
        return when (diff) {
            0L -> "Heute"
            1L -> "Morgen"
            -1L -> "Gestern"
            else -> "${shortWeekdays[date.dayOfWeekMondayFirst()]}, ${date.day}. ${shortMonths[(date.month - 1).coerceIn(0, 11)]}"
        }
    }

    fun formatFull(date: SimpleDate): String {
        val wd = shortWeekdays[date.dayOfWeekMondayFirst()]
        return "$wd, ${date.day}. ${monthName(date.month)} ${date.year}"
    }

    fun formatNumeric(date: SimpleDate): String {
        val d = date.day.toString().padStart(2, '0')
        val m = date.month.toString().padStart(2, '0')
        return "$d.$m.${date.year}"
    }

    fun currentWeekRange(today: SimpleDate = SimpleDate.today()): LongRange {
        val monday = today.toEpochDay() - today.dayOfWeekMondayFirst().toLong()
        return monday..(monday + 6L)
    }
}
