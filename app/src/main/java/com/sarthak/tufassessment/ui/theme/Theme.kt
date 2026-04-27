package com.sarthak.tufassessment.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WhatsAppGreen,
    onPrimary = WhatsAppTextPrimary,
    primaryContainer = WhatsAppDarkOutgoingBubble,
    onPrimaryContainer = WhatsAppDarkTextPrimary,
    secondary = WhatsAppGreenDark,
    onSecondary = WhatsAppTextPrimary,
    tertiary = WhatsAppGreenLight,
    background = WhatsAppDarkBackground,
    onBackground = WhatsAppDarkTextPrimary,
    surface = WhatsAppDarkSurface,
    onSurface = WhatsAppDarkTextPrimary,
    surfaceVariant = WhatsAppDarkSurfaceVariant,
    onSurfaceVariant = WhatsAppDarkTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = WhatsAppGreenDark,
    onPrimary = WhatsAppSurface,
    primaryContainer = WhatsAppOutgoingBubble,
    onPrimaryContainer = WhatsAppTextPrimary,
    secondary = WhatsAppGreen,
    onSecondary = WhatsAppSurface,
    tertiary = WhatsAppGreenLight,
    background = WhatsAppBackground,
    onBackground = WhatsAppTextPrimary,
    surface = WhatsAppSurface,
    onSurface = WhatsAppTextPrimary,
    surfaceVariant = WhatsAppSurfaceVariant,
    onSurfaceVariant = WhatsAppTextSecondary
)

@Composable
fun TUFAssessmentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
