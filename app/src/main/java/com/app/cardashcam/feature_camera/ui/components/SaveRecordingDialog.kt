package com.app.cardashcam.feature_camera.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.app.cardashcam.core.ui.theme.*

@Composable
fun SaveRecordingDialog(
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .background(SurfaceGlass, RoundedCornerShape(24.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "Save Recording?",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            
            Text(
                "Do you want to keep this recording?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = RecordingRed
                    )
                ) {
                    Text("Delete")
                }
                
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonBlue
                    )
                ) {
                    Text("Save", color = TextPrimary)
                }
            }
        }
    }
}
