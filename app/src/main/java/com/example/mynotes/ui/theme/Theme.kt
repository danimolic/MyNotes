package com.example.mynotes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Ink,
    onPrimary = EditorTan,
    background = EditorTan,
    surface = PaperTan,
    onSurface = Ink,
    error = ErrorRed
)

@Composable
fun MyNotesTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, typography = Typography, content = content)
}
