package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.model.ThemeMode

private val StudioDarkColorScheme = darkColorScheme(
    primary = StudioCoralDark,
    onPrimary = Color(0xFF280501),
    primaryContainer = StudioCoralContainerDark,
    onPrimaryContainer = StudioOnCoralContainerDark,
    secondary = StudioAmberDark,
    onSecondary = Color(0xFF2B1500),
    secondaryContainer = StudioAmberContainerDark,
    onSecondaryContainer = StudioOnAmberContainerDark,
    tertiary = StudioEmeraldDark,
    onTertiary = Color(0xFF002116),
    tertiaryContainer = StudioEmeraldContainerDark,
    onTertiaryContainer = StudioOnEmeraldContainerDark,
    background = StudioDarkBackground,
    onBackground = StudioDarkOnBackground,
    surface = StudioDarkSurface,
    onSurface = StudioDarkOnSurface,
    surfaceVariant = StudioDarkSurfaceVariant,
    onSurfaceVariant = StudioDarkOnSurfaceVariant,
    surfaceContainerLow = StudioDarkSurfaceContainerLow,
    surfaceContainer = StudioDarkSurfaceContainer,
    surfaceContainerHigh = StudioDarkSurfaceContainerHigh,
    outline = StudioDarkOutline,
    outlineVariant = StudioDarkOutlineVariant,
    error = Color(0xFFFF6E6E),
    onError = Color(0xFF3A0000)
)

private val StudioLightColorScheme = lightColorScheme(
    primary = StudioCoralLight,
    onPrimary = Color.White,
    primaryContainer = StudioCoralContainerLight,
    onPrimaryContainer = StudioOnCoralContainerLight,
    secondary = StudioAmberLight,
    onSecondary = Color.White,
    secondaryContainer = StudioAmberContainerLight,
    onSecondaryContainer = StudioOnAmberContainerLight,
    tertiary = StudioEmeraldLight,
    onTertiary = Color.White,
    tertiaryContainer = StudioEmeraldContainerLight,
    onTertiaryContainer = StudioOnEmeraldContainerLight,
    background = StudioLightBackground,
    onBackground = StudioLightOnBackground,
    surface = StudioLightSurface,
    onSurface = StudioLightOnSurface,
    surfaceVariant = StudioLightSurfaceVariant,
    onSurfaceVariant = StudioLightOnSurfaceVariant,
    surfaceContainerLow = StudioLightSurfaceContainerLow,
    surfaceContainer = StudioLightSurfaceContainer,
    surfaceContainerHigh = StudioLightSurfaceContainerHigh,
    outline = StudioLightOutline,
    outlineVariant = StudioLightOutlineVariant,
    error = Color(0xFFC62828),
    onError = Color.White
)

private val StudioShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun CreatorDeskTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (useDarkTheme) StudioDarkColorScheme else StudioLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !useDarkTheme
                controller.isAppearanceLightNavigationBars = !useDarkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CreatorDeskTypography,
        shapes = StudioShapes,
        content = content
    )
}
