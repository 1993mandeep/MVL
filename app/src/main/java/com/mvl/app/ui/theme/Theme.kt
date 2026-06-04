package com.mvl.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MVLColorScheme = lightColorScheme(
    primary = Color(0xFFFFC300),
    onPrimary = Color(0xFF1A1A1A),
    secondary = Color(0xFF1A1A1A),
    background = Color(0xFFFAFAFA),
    surface = Color.White
)

@Composable
fun MVLAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MVLColorScheme,
        content = content
    )
}
