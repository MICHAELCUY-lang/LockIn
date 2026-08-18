package com.example.lockin.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Organic Light Palette ──────────────────────────────────────────────────
val Primary = Color(0xFF5A6332)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFA2AB73)
val OnPrimaryContainer = Color(0xFF383F12)

val Secondary = Color(0xFF5D604B)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFE2E5C9)
val OnSecondaryContainer = Color(0xFF636650)

val Tertiary = Color(0xFF735477)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFBF9AC1)
val OnTertiaryContainer = Color(0xFF4E3152)

val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

val Background = Color(0xFFFCF9F2)
val OnBackground = Color(0xFF1C1C18)
val Surface = Color(0xFFFCF9F2)
val OnSurface = Color(0xFF1C1C18)
val SurfaceVariant = Color(0xFFE5E2DB)
val OnSurfaceVariant = Color(0xFF47483C)
val Outline = Color(0xFF77786B)
val OutlineVariant = Color(0xFFC8C7B8)

val SurfaceContainer = Color(0xFFF0EEE7)
val SurfaceContainerHigh = Color(0xFFEBE8E1)
val SurfaceContainerHighest = Color(0xFFE5E2DB)

private val OrganicLightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant
)

@Composable
fun LockInTheme(content: @Composable () -> Unit) {
    val colorScheme = OrganicLightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Background.toArgb()
            window.navigationBarColor = SurfaceContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = true
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// ── Legacy Aliases to prevent compilation errors ──
val Violet = Primary
val VioletLight = PrimaryContainer
val VioletDark = OnPrimaryContainer
val Cyan = Secondary
val Pink = Tertiary
val Emerald = Primary
val Coral = Error
val Surface0 = Surface
val Surface1 = SurfaceContainer
val Surface2 = SurfaceContainerHigh
val Muted = OnSurfaceVariant
