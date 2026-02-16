package com.app.cardashcam.core.ui.components.inputs

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.app.cardashcam.core.ui.theme.*

@Composable
fun NeonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false
) {
    var visible by remember { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }

    val borderColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (focused) NeonBlue else GlassStroke,
        animationSpec = tween(300),
        label = "border"
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused }
            .shadow(
                elevation = if (focused) 8.dp else 0.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = NeonBlue.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        visualTransformation = if (isPassword && !visible)
            PasswordVisualTransformation()
        else VisualTransformation.None,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = NeonBlue,
            unfocusedLabelColor = TextSecondary
        )
    )
}
