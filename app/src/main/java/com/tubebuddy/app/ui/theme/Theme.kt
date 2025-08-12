package com.tubebuddy.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Gray50,
    onPrimary = Gray10,

    secondary = Gray60,
    onSecondary = Gray10,

    tertiary = Orange20,
    onTertiary = Gray10,

    background = Gray70,
    onBackground = Gray10,

    surface = Gray80,
    onSurface = Gray10
)

private val LightColorScheme = lightColorScheme(
    primary = Gray30,
    onPrimary = Gray10,

    secondary = Gray40,
    onSecondary = Gray10,

    tertiary = Orange30,
    onTertiary = Gray80,

    background = Gray50,
    onBackground = Gray60,

    surface = Gray60,
    onSurface = Gray10
)

@Composable
fun TubeBuddyTheme(
    isDarkTheme: Boolean,
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

        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

object ThemeStateHolder {
    val isDarkTheme = mutableStateOf(false)
}