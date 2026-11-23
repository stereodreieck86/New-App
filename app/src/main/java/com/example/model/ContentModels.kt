package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.PlatformInstagram
import com.example.ui.theme.PlatformLinkedIn
import com.example.ui.theme.PlatformPodcast
import com.example.ui.theme.PlatformShorts
import com.example.ui.theme.PlatformTikTok
import com.example.ui.theme.PlatformYouTube
import kotlin.math.ceil

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    fun next(): ThemeMode = when (this) {
        SYSTEM -> DARK
        DARK -> LIGHT
        LIGHT -> SYSTEM
    }
}

enum class ContentPlatform(
    val displayName: String,
    val shortName: String,
    val accentColor: Color,
    val icon: ImageVector
) {
    YOUTUBE("YouTube", "YT", PlatformYouTube, Icons.Rounded.PlayArrow),
    TIKTOK("TikTok", "TT", PlatformTikTok, Icons.Rounded.MusicNote),
    INSTAGRAM("Instagram", "IG", PlatformInstagram, Icons.Rounded.CameraAlt),
    SHORTS("Shorts / Reels", "Short", PlatformShorts, Icons.Rounded.Bolt),
    LINKEDIN("LinkedIn", "IN", PlatformLinkedIn, Icons.Rounded.Work),
    PODCAST("Podcast", "Pod", PlatformPodcast, Icons.Rounded.Mic);

    companion object {
        fun fromKey(key: String): ContentPlatform =
            entries.firstOrNull { it.name == key } ?: YOUTUBE
    }
}

enum class ContentStatus(
    val displayName: String,
    val shortLabel: String,
    val progressWeight: Float,
    val icon: ImageVector
) {
    IDEA("Idee", "Idee", 0.2f, Icons.Rounded.Lightbulb),
    SCRIPTING("Hook & Skript", "Skript", 0.45f, Icons.Rounded.EditNote),
    FILMING("Produktion", "Dreh", 0.7f, Icons.Rounded.Videocam),
    SCHEDULED("Geplant", "Geplant", 0.88f, Icons.Rounded.EventAvailable),
    PUBLISHED("Veröffentlicht", "Live", 1.0f, Icons.Rounded.CheckCircle);

    fun next(): ContentStatus? = when (this) {
        IDEA -> SCRIPTING
        SCRIPTING -> FILMING
        FILMING -> SCHEDULED
        SCHEDULED -> PUBLISHED
        PUBLISHED -> null
    }

    companion object {
        fun fromKey(key: String): ContentStatus =
            entries.firstOrNull { it.name == key } ?: IDEA
    }
}

enum class HookStyle(
    val displayName: String,
    val badgeText: String
) {
    CURIOSITY("Neugier-Lücke", "Neugier"),
    CONTRARIAN("Kontroverse These", "Kontrovers"),
    STORY("Story-Einstieg", "Story"),
    LISTICLE("Zahlen & Tipps", "Liste"),
    PROBLEM("Problem & Lösung", "Problem"),
    CUSTOM("Freier Stil", "Frei");

    companion object {
        fun fromKey(key: String): HookStyle =
            entries.firstOrNull { it.name == key } ?: CURIOSITY
    }
}

data class HookTemplate(
    val id: String,
    val style: HookStyle,
    val title: String,
    val formula: String,
    val tip: String,
    val icon: ImageVector = Icons.Rounded.AutoAwesome
)

val DefaultHookTemplates = listOf(
    HookTemplate(
        id = "curiosity_secret",
        style = HookStyle.CURIOSITY,
        title = "Das übersehene Detail",
        formula = "Fast niemand spricht darüber, aber genau dieser eine Fehler kostet dich …",
        tip = "Weckt sofort Neugier in den ersten 2 Sekunden."
    ),
    HookTemplate(
        id = "contrarian_stop",
        style = HookStyle.CONTRARIAN,
        title = "Musterbrecher",
        formula = "Hör sofort auf, … zu machen – mach stattdessen lieber das hier:",
        tip = "Stoppt das Scrollen durch eine klare Gegenposition."
    ),
    HookTemplate(
        id = "listicle_fast",
        style = HookStyle.LISTICLE,
        title = "Kompakte Liste",
        formula = "3 einfache Tricks für …, die du in unter 60 Sekunden umsetzen kannst:",
        tip = "Klare Zahl + schneller Nutzen steigert die Watchtime."
    ),
    HookTemplate(
        id = "story_result",
        style = HookStyle.STORY,
        title = "Vorher / Nachher",
        formula = "Vor 30 Tagen hatte ich noch …, und heute zeige ich dir genau, was alles verändert hat.",
        tip = "Persönliche Transformation baut sofort Vertrauen auf."
    ),
    HookTemplate(
        id = "problem_fix",
        style = HookStyle.PROBLEM,
        title = "Direktes Problem",
        formula = "Wenn deine Videos nach 3 Sekunden weggeklickt werden, liegt es meistens an diesem Punkt:",
        tip = "Spricht deine Zielgruppe punktgenau beim Schmerzpunkt an."
    )
)

data class ContentItem(
    val id: Long = 0L,
    val title: String,
    val hook: String = "",
    val hookStyle: HookStyle = HookStyle.CURIOSITY,
    val notes: String = "",
    val platform: ContentPlatform = ContentPlatform.YOUTUBE,
    val status: ContentStatus = ContentStatus.IDEA,
    val scheduledDateEpochDay: Long? = null,
    val scheduledTimeText: String = "",
    val isPinned: Boolean = false,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val updatedAtMillis: Long = System.currentTimeMillis()
) {
    val hasHook: Boolean
        get() = hook.isNotBlank()

    fun hookWordCount(): Int {
        val trimmed = hook.trim()
        if (trimmed.isEmpty()) return 0
        return trimmed.split(Regex("\\s+")).size
    }

    fun estimatedHookSeconds(): Int {
        val words = hookWordCount()
        if (words == 0) return 0
        return ceil(words / 2.4).toInt().coerceAtLeast(1)
    }
}
