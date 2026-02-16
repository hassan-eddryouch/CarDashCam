package com.app.cardashcam.feature_camera.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.cardashcam.core.ui.theme.*

@Composable
fun RightSpeedPanel(
    currentSpeed: Float?,
    maxSpeed: Float = 0f,
    tripDuration: String = "00:00",
    distance: Float = 0f,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(end = 16.dp)
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.7f),
                        Color.Black.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current Speed (Large)
        SpeedDisplay(
            speed = currentSpeed,
            label = "km/h",
            isMain = true
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Max Speed
        StatItem(
            label = "MAX",
            value = if (maxSpeed > 0) "%.0f".format(maxSpeed) else "--"
        )

        // Trip Duration
        StatItem(
            label = "TIME",
            value = tripDuration
        )

        // Distance
        StatItem(
            label = "DIST",
            value = if (distance > 0) "%.1f km".format(distance) else "-- km"
        )
    }
}

@Composable
private fun SpeedDisplay(
    speed: Float?,
    label: String,
    isMain: Boolean
) {
    val displaySpeed = speed?.toInt() ?: 0
    
    val animatedSpeed by animateIntAsState(
        targetValue = displaySpeed,
        animationSpec = tween(300),
        label = "speed"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (speed != null) animatedSpeed.toString() else "--",
            color = if (isMain) NeonBlue else TextPrimary,
            fontSize = if (isMain) 48.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = label,
            color = TextSecondary,
            fontSize = if (isMain) 14.sp else 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = label,
            color = TextTertiary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace
        )
    }
}
