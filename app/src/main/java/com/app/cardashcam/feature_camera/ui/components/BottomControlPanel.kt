package com.app.cardashcam.feature_camera.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.cardashcam.core.ui.theme.*

@Composable
fun BottomControlPanel(
    isRecording: Boolean,
    isMicEnabled: Boolean,
    onRecordClick: () -> Unit,
    onLockClick: () -> Unit,
    onMicToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 32.dp, start = 24.dp, end = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lock Button
        ControlButton(
            icon = Icons.Default.Lock,
            onClick = onLockClick,
            tint = LockedGold
        )

        // Record Button (Large, Center)
        PulsingRecordButton(
            isRecording = isRecording,
            onClick = onRecordClick
        )

        // Microphone Toggle
        ControlButton(
            icon = if (isMicEnabled) Icons.Default.Mic else Icons.Default.MicOff,
            onClick = onMicToggle,
            tint = if (isMicEnabled) NeonBlue else TextTertiary
        )
    }
}

@Composable
private fun PulsingRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect when recording
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(scale)
                    .blur(20.dp)
                    .background(
                        color = RecordingRed.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
        }

        // Main button
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(16.dp, CircleShape)
                .background(
                    color = if (isRecording) RecordingRed else NeonBlue,
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            // Inner circle or square
            Box(
                modifier = Modifier
                    .size(if (isRecording) 28.dp else 0.dp)
                    .background(
                        color = Color.White,
                        shape = if (isRecording) RoundedCornerShape(6.dp) else CircleShape
                    )
            )
        }
    }
}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .shadow(8.dp, CircleShape)
            .background(Glass, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(26.dp)
        )
    }
}
