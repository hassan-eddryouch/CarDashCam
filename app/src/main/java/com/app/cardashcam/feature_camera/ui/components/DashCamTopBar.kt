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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.cardashcam.core.ui.theme.*

@Composable
fun DashCamTopBar(
    recordingTime: String,
    isRecording: Boolean,
    hasGPS: Boolean,
    onGalleryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // GPS Status
        GPSIndicator(hasGPS = hasGPS)

        // Recording Timer
        if (isRecording) {
            RecordingTimerCapsule(time = recordingTime)
        }

        // Action Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HudIconButton(
                icon = Icons.Default.PhotoLibrary,
                onClick = onGalleryClick
            )
            HudIconButton(
                icon = Icons.Default.Settings,
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun GPSIndicator(hasGPS: Boolean) {
    Row(
        modifier = Modifier
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .background(
                color = if (hasGPS) NeonBlue.copy(alpha = 0.2f) else Glass,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.GpsFixed,
            contentDescription = null,
            tint = if (hasGPS) NeonBlue else TextTertiary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = if (hasGPS) "GPS" else "NO GPS",
            color = if (hasGPS) NeonBlue else TextTertiary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecordingTimerCapsule(time: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(RecordingRed.copy(alpha = alpha), CircleShape)
        )
        Text(
            text = time,
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HudIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .shadow(6.dp, CircleShape)
            .background(Glass, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NeonBlue,
            modifier = Modifier.size(22.dp)
        )
    }
}
