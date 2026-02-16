package com.app.cardashcam.feature_camera.ui

import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.app.cardashcam.feature_camera.camera.DashCamController
import kotlinx.coroutines.launch

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onReady: (DashCamController) -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->

            val previewView = PreviewView(ctx)

            val controller = DashCamController(
                context,
                lifecycleOwner,
                previewView
            )

            lifecycleOwner.lifecycleScope.launch {
                controller.startCamera()
                onReady(controller)
            }

            previewView
        }
    )
}
