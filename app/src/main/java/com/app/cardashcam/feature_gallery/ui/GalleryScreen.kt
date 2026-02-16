package com.app.cardashcam.feature_gallery.ui

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.cardashcam.core.ui.theme.Night
import com.app.cardashcam.feature_gallery.utils.VideoFilter
import com.app.cardashcam.feature_gallery.viewmodel.GalleryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onBack: () -> Unit,
    onVideoClick: (Uri) -> Unit
) {

    val vm: GalleryViewModel = viewModel()
    val groupedVideos by vm.groupedVideos.collectAsStateWithLifecycle()
    val isLoading by vm.isLoading.collectAsStateWithLifecycle()
    val currentFilter by vm.currentFilter.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { 
        vm.load() 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recordings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Night
                )
            )
        },
        containerColor = Night
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = currentFilter == VideoFilter.ALL,
                        onClick = { vm.setFilter(VideoFilter.ALL) },
                        label = { Text("All") }
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == VideoFilter.LOCKED,
                        onClick = { vm.setFilter(VideoFilter.LOCKED) },
                        label = { Text("Locked") }
                    )
                }
                item {
                    FilterChip(
                        selected = currentFilter == VideoFilter.EVENTS,
                        onClick = { vm.setFilter(VideoFilter.EVENTS) },
                        label = { Text("Events") }
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (groupedVideos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No recordings yet",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    groupedVideos.forEach { group ->
                        item {
                            Text(
                                text = group.group.name,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = group.videos,
                                    key = { it.uri.toString() }
                                ) { video ->
                                    VideoCard(
                                        video = video,
                                        onOpen = { onVideoClick(video.uri) },
                                        onDelete = { vm.delete(video.uri) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
