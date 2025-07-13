package com.example.tubebuddy.ui.theme

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
    primary = Blue05,
    secondary = Blue05,
    tertiary = Orange30,
    background = Gray20,
    onBackground = Gray90,
    onPrimary = Gray05,
    onSecondary = Gray30,
    onTertiary = Blue40,
    surface = Blue10,
    onSurface = Gray10
)

private val LightColorScheme = lightColorScheme(
    primary = Blue05,
    secondary = Blue05,
    tertiary = Orange30,
    background = Gray20,
    onBackground = Gray90,
    onPrimary = Gray05,
    onSecondary = Gray30,
    onTertiary = Blue40,
    surface = Blue10,
    onSurface = Gray10
)

@Composable
fun TubeBuddyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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