package com.app.cardashcam.feature_camera.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.cardashcam.feature_camera.sensors.CrashDetector
import com.app.cardashcam.feature_camera.service.RecordingService
import com.app.cardashcam.feature_camera.state.RecordingTimer
import com.app.cardashcam.feature_camera.ui.components.*
import com.app.cardashcam.feature_camera.viewmodel.CameraViewModel
import com.google.accompanist.permissions.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(openGallery: () -> Unit) {

    val context = LocalContext.current
    val vm: CameraViewModel = viewModel()
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val timer = remember { RecordingTimer() }
    var recordingService by remember { mutableStateOf<RecordingService?>(null) }

    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as RecordingService.RecordingBinder
                recordingService = binder.getService()
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                recordingService = null
            }
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, RecordingService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        onDispose {
            context.unbindService(serviceConnection)
        }
    }

    LaunchedEffect(uiState.isRecording) {
        if (uiState.isRecording) {
            timer.start()
            while (uiState.isRecording) {
                val time = timer.getTime()
                vm.updateRecordingTime(time)
                recordingService?.updateNotification(time)
                delay(1000)
            }
        } else {
            timer.stop()
        }
    }

    LaunchedEffect(permissionState.permissions) {
        if (permissionState.permissions.any { it.permission == Manifest.permission.ACCESS_FINE_LOCATION && it.status.isGranted }) {
            vm.startLocationUpdates()
        }
    }

    val crashDetector = remember {
        CrashDetector(context) {
            recordingService?.lockVideo()
            vm.setLocked(true)
        }
    }

    DisposableEffect(crashDetector) {
        crashDetector.start()
        onDispose { crashDetector.stop() }
    }

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    if (!permissionState.permissions.any { it.permission == Manifest.permission.CAMERA && it.status.isGranted }) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0A1628)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Text(
                "Camera permission required",
                color = Color.White
            )
        }
        return
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1628),
                        Color(0xFF05070A)
                    )
                )
            )
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            update = { previewView ->
                recordingService?.setPreviewSurface(previewView.surfaceProvider)
            }
        )

        DashCamTopBar(
            recordingTime = uiState.recordingTime,
            isRecording = uiState.isRecording,
            hasGPS = uiState.hasGPS,
            onGalleryClick = openGallery,
            onSettingsClick = { },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        CenterHudOverlay(
            isRecording = uiState.isRecording,
            direction = "N",
            modifier = Modifier.align(Alignment.TopCenter)
        )

        RightSpeedPanel(
            currentSpeed = uiState.speed,
            maxSpeed = uiState.maxSpeed,
            tripDuration = uiState.tripDuration,
            distance = uiState.distance,
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        BottomControlPanel(
            isRecording = uiState.isRecording,
            isMicEnabled = uiState.isMicEnabled,
            onRecordClick = {
                recordingService?.let { service ->
                    if (service.isRecordingActive()) {
                        service.stopRecordingCommand()
                        vm.stopRecording()
                        Intent(context, RecordingService::class.java).apply {
                            action = RecordingService.ACTION_STOP_RECORDING
                        }.also { context.startService(it) }
                    } else {
                        Intent(context, RecordingService::class.java).apply {
                            action = RecordingService.ACTION_START_RECORDING
                        }.also { ContextCompat.startForegroundService(context, it) }
                        service.startRecordingCommand()
                        vm.startRecording()
                    }
                }
            },
            onLockClick = {
                recordingService?.lockVideo()
                vm.setLocked(true)
            },
            onMicToggle = {
                vm.toggleMicrophone()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (uiState.showSaveDialog) {
        SaveRecordingDialog(
            onSave = {
                recordingService?.saveRecording()
                vm.confirmSave()
            },
            onDelete = {
                recordingService?.deleteRecording()
                vm.confirmDelete()
            },
            onDismiss = {
                recordingService?.saveRecording()
                vm.dismissSaveDialog()
            }
        )
    }
}
