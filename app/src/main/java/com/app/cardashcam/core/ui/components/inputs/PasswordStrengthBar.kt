package com.app.cardashcam.core.ui.components.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.cardashcam.core.ui.theme.GlassStroke

@Composable
fun PasswordStrengthBar(password: String) {

    val strength = when {
        password.length < 6 -> 0f
        password.length < 8 -> 0.33f
        password.any { it.isDigit() } && password.any { it.isUpperCase() } -> 1f
        else -> 0.66f
    }

    val color = when {
        strength < 0.34f -> Color.Red
        strength < 0.67f -> Color.Yellow
        else -> Color.Green
    }

    Box(
        Modifier
            .fillMaxWidth()
            .height(6.dp)
            .background(GlassStroke, RoundedCornerShape(10.dp))
    ) {
        Box(
            Modifier
                .fillMaxWidth(strength)
                .height(6.dp)
                .background(color, RoundedCornerShape(10.dp))
        )
    }
}
