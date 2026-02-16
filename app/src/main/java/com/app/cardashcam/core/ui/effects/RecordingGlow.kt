package com.app.cardashcam.core.ui.effects

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.Stroke
import com.app.cardashcam.core.ui.theme.RecordingRed

fun Modifier.recordingGlow(active: Boolean): Modifier = drawWithContent {
    drawContent()

    if (active) {
        drawRect(
            color = RecordingRed.copy(alpha = 0.18f),
            size = size,
            style = Stroke(width = 18f)
        )
    }
}
