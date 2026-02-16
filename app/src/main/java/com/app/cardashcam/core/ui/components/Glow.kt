package com.app.cardashcam.core.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color

fun Modifier.neonGlow(color: Color): Modifier = this.drawBehind {
    drawCircle(
        color = color.copy(alpha = 0.25f),
        radius = size.maxDimension
    )
}
