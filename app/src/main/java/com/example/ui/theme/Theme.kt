package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8A80),
    onPrimary = Color(0xFF5C0003),
    primaryContainer = Color(0xFF7E0B0F),
    onPrimaryContainer = Color(0xFFFFDAD8),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF4A2800),
    secondaryContainer = Color(0xFF6B3C00),
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = Color(0xFF82B1FF),
    onTertiary = Color(0xFF002B73),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE2E2E2),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE2E2E2),
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFC7C7C7),
    outline = Color(0xFF8E8E93),
)

private val LightColorScheme = lightColorScheme(
    primary = HotmailerRed,
    onPrimary = Color.White,
    primaryContainer = HotmailerRedLight,
    onPrimaryContainer = HotmailerRedDark,
    secondary = HotmailerOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFF3E0),
    onSecondaryContainer = Color(0xFFE65100),
    tertiary = PrimaryBlue,
    onTertiary = Color.White,
    background = Color(0xFFF9FAFB),
    onBackground = Color(0xFF111827),
    surface = Color.White,
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
)

@Composable
fun HotmailerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep Hotmailer identity distinctive
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
