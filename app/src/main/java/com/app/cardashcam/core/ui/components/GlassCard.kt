package com.app.cardashcam.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.cardashcam.core.ui.theme.Glass
import com.app.cardashcam.core.ui.theme.GlassStroke

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "scale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .background(
                color = Glass,
                shape = RoundedCornerShape(24.dp)
            )
            .border(
                width = 1.dp,
                color = GlassStroke,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        content()
    }
}
