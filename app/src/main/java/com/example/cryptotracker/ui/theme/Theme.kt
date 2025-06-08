package com.example.cryptotracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun CryptoTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Primary,
            secondary = Secondary,
            background = BackgroundDark,
            surface = SurfaceDark
        )
    } else {
        lightColorScheme(
            primary = Primary,
            secondary = Secondary,
            background = BackgroundLight,
            surface = SurfaceLight
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}