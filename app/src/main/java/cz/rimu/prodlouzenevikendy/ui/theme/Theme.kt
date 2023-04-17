package cz.rimu.prodlouzenevikendy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = lightBlue200,
    onPrimary = Color.Black,
    primaryVariant = lightBlue700,
    secondary = amber200,
    secondaryVariant = amber200,
)

private val LightColorPalette = lightColors(
    primary = lightBlue500,
    onPrimary = Color.Black,
    primaryVariant = lightBlue700,
    secondary = amber200,
    secondaryVariant = amber700,
)

@Composable
fun ProdlouzeneVikendyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}