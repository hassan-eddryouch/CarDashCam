package com.app.cardashcam.feature_camera.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.cardashcam.core.ui.theme.RecordingRed
import com.app.cardashcam.core.ui.theme.TextPrimary
import com.app.cardashcam.core.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CenterHudOverlay(
    isRecording: Boolean,
    direction: String = "N",
    modifier: Modifier = Modifier
) {
    val currentTime by produceState(initialValue = getCurrentDateTime()) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            value = getCurrentDateTime()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // REC Indicator
        if (isRecording) {
            BlinkingRecIndicator()
        }

        // Date & Time
        Text(
            text = currentTime.first,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = currentTime.second,
            color = TextSecondary,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace
        )

        // Direction
        Text(
            text = "↑ $direction",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BlinkingRecIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "rec_blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(RecordingRed.copy(alpha = alpha), CircleShape)
        )
        Text(
            text = "REC",
            color = RecordingRed.copy(alpha = alpha),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

private fun getCurrentDateTime(): Pair<String, String> {
    val now = Date()
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return Pair(timeFormat.format(now), dateFormat.format(now))
}
