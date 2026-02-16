package com.app.cardashcam.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.cardashcam.core.ui.theme.NeonBlue
import com.app.cardashcam.core.ui.theme.TextPrimary

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    var pressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Button(
        onClick = {
            pressed = true
            onClick()
        },
        enabled = enabled && !loading,
        modifier = modifier
            .scale(scale)
            .height(56.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = NeonBlue.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NeonBlue,
            disabledContainerColor = NeonBlue.copy(alpha = 0.5f)
        )
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = TextPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text,
                color = TextPrimary,
                style = androidx.compose.material3.MaterialTheme.typography.labelLarge
            )
        }
    }
    
    LaunchedEffect(pressed) {
        if (pressed) {
            kotlinx.coroutines.delay(100)
            pressed = false
        }
    }
}
