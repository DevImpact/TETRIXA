package com.game.tetrixa.devimpact.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.game.tetrixa.devimpact.R

@Composable
fun TetrixaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = colorResource(R.color.color_primary),
            secondary = colorResource(R.color.color_secondary),
            background = colorResource(R.color.on_background),
            surface = colorResource(R.color.brand_secondary),
            onPrimary = colorResource(R.color.white),
            onSecondary = colorResource(R.color.white),
            onBackground = colorResource(R.color.white),
            onSurface = colorResource(R.color.white),
            error = colorResource(R.color.color_error),
            onError = colorResource(R.color.on_error)
        )
    } else {
        lightColorScheme(
            primary = colorResource(R.color.color_primary),
            secondary = colorResource(R.color.color_secondary),
            background = colorResource(R.color.background),
            surface = colorResource(R.color.surface),
            onPrimary = colorResource(R.color.white),
            onSecondary = colorResource(R.color.white),
            onBackground = colorResource(R.color.on_background),
            onSurface = colorResource(R.color.on_surface),
            error = colorResource(R.color.color_error),
            onError = colorResource(R.color.on_error)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
