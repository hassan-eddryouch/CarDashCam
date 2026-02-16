package com.app.cardashcam.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displayLarge = TextStyle(
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary,
        letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontSize = 28.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
        color = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        color = TextSecondary
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = TextPrimary,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = TextSecondary
    )
)
