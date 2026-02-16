package com.app.cardashcam.feature_gallery.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.cardashcam.core.ui.theme.*
import com.app.cardashcam.domain.model.VideoItem
import com.app.cardashcam.feature_gallery.utils.VideoFormatters

@Composable
fun VideoCard(
    video: VideoItem,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = NeonBlue.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Glass.copy(alpha = 0.2f),
                        Glass.copy(alpha = 0.1f)
                    )
                )
            )
            .clickable(onClick = onOpen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = video.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = VideoFormatters.formatTime(video.date),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary
                    )
                    Text(
                        text = VideoFormatters.formatDate(video.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                
                if (video.isLocked) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(LockedGold.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = LockedGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = NeonBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = VideoFormatters.formatDuration(video.duration),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = NeonBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = VideoFormatters.formatSize(video.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
        
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(36.dp)
                .background(RecordingRed.copy(alpha = 0.2f), CircleShape)
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = RecordingRed,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
