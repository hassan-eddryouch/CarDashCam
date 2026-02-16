package com.app.cardashcam.feature_gallery.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.app.cardashcam.core.ui.theme.NeonBlue
import com.app.cardashcam.core.ui.theme.NeonBlueGlow

@Composable
fun GlowingIconContainer(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(56.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .blur(12.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonBlueGlow, NeonBlue.copy(alpha = 0.3f))
                    ),
                    shape = CircleShape
                )
        )
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(8.dp, CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            NeonBlue.copy(alpha = 0.4f),
                            NeonBlue.copy(alpha = 0.2f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = NeonBlueGlow,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
