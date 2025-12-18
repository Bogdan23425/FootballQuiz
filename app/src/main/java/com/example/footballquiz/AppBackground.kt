package com.example.footballquiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme

@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val base = MaterialTheme.colorScheme.background

    val gradient = Brush.verticalGradient(
        listOf(
            primary.copy(alpha = 0.15f),
            base,
            secondary.copy(alpha = 0.08f)
        )
    )

    Box(modifier = modifier.fillMaxSize().background(gradient)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f)
        ) {
            drawSoftCircle(
                color = primary.copy(alpha = 0.12f),
                radius = size.minDimension * 0.55f,
                center = Offset(x = size.width * 0.85f, y = size.height * 0.15f)
            )
            drawSoftCircle(
                color = secondary.copy(alpha = 0.08f),
                radius = size.minDimension * 0.45f,
                center = Offset(x = size.width * 0.1f, y = size.height * 0.8f)
            )
        }
        Box(modifier = Modifier.fillMaxSize().zIndex(1f), content = content)
    }
}

private fun DrawScope.drawSoftCircle(color: Color, radius: Float, center: Offset) {
    drawCircle(color = color, radius = radius, center = center)
}
