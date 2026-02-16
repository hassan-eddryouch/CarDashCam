package com.app.cardashcam.feature_player.ui

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.app.cardashcam.feature_player.ui.components.PlayerHudOverlay

@Composable
fun PlayerScreen(uri: Uri, onBack: () -> Unit = {}) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var savedPosition by rememberSaveable { mutableStateOf(0L) }
    val isLocked = remember { uri.toString().contains("_LOCK") }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            if (savedPosition > 0) seekTo(savedPosition)
            playWhenReady = true
            
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }
            })
        }
    }

    LaunchedEffect(player) {
        while (true) {
            currentPosition = player.currentPosition
            duration = player.duration.coerceAtLeast(0L)
            kotlinx.coroutines.delay(500)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    savedPosition = player.currentPosition
                    player.pause()
                }
                Lifecycle.Event.ON_RESUME -> {
                    if (savedPosition > 0) player.seekTo(savedPosition)
                }
                Lifecycle.Event.ON_DESTROY -> {
                    player.release()
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            player.release()
        }
    }

    BackHandler {
        onBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    useController = false
                }
            }
        )

        PlayerHudOverlay(
            isPlaying = isPlaying,
            currentPosition = currentPosition,
            duration = duration,
            isLocked = isLocked,
            onPlayPause = {
                if (player.isPlaying) player.pause() else player.play()
            },
            onSeek = { position ->
                player.seekTo(position)
            },
            onBack = onBack
        )
    }
}
