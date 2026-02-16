package com.app.cardashcam.feature_camera.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.camera.core.Preview
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.app.cardashcam.MainActivity
import com.app.cardashcam.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RecordingService : Service(), LifecycleOwner {

    private val binder = RecordingBinder()
    private var isRecording = false
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var cameraController: ServiceCameraController
    private val lifecycleRegistry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    inner class RecordingBinder : Binder() {
        fun getService(): RecordingService = this@RecordingService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        createNotificationChannel()
        cameraController = ServiceCameraController(this, this)
        serviceScope.launch {
            cameraController.initialize()
        }
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_RECORDING -> startRecording()
            ACTION_STOP_RECORDING -> stopRecording()
        }
        return START_STICKY
    }

    fun setPreviewSurface(surfaceProvider: Preview.SurfaceProvider) {
        cameraController.setPreviewSurface(surfaceProvider)
    }

    fun startRecordingCommand() {
        cameraController.startRecording {
            isRecording = true
        }
    }

    fun stopRecordingCommand() {
        cameraController.stopRecording()
        isRecording = false
    }

    fun lockVideo() {
        cameraController.lockNextVideo()
    }

    fun saveRecording(): Boolean {
        return cameraController.saveRecording()
    }

    fun deleteRecording() {
        cameraController.deleteRecording()
    }

    fun isRecordingActive() = cameraController.isRecording()

    private fun startRecording() {
        if (isRecording) return
        
        val notification = createNotification("Recording...")
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun stopRecording() {
        isRecording = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    fun updateNotification(time: String) {
        if (!isRecording) return
        
        val notification = createNotification("Recording: $time")
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(contentText: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, RecordingService::class.java).apply {
            action = ACTION_STOP_RECORDING
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("CarDashCam")
            .setContentText(contentText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Stop",
                stopPendingIntent
            )
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recording Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows recording status"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        cameraController.release()
    }

    companion object {
        private const val CHANNEL_ID = "recording_channel"
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START_RECORDING = "START_RECORDING"
        const val ACTION_STOP_RECORDING = "STOP_RECORDING"
    }
}
