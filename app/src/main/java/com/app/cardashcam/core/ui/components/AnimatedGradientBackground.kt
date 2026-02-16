package com.app.cardashcam.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.app.cardashcam.core.ui.theme.DeepNavy
import com.app.cardashcam.core.ui.theme.Night
import com.app.cardashcam.core.ui.theme.NeonBlue

@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(
                    Night,
                    DeepNavy,
                    Night,
                    DeepNavy.copy(alpha = 0.6f),
                    Night
                ),
                start = Offset(0f, offset * 2000),
                end = Offset(1000f, (1 - offset) * 2000)
            )
        )
    ) {
        content()
    }
}
