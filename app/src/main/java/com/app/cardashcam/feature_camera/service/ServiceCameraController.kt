package com.app.cardashcam.feature_camera.service

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.app.cardashcam.core.storage.StorageManager
import com.app.cardashcam.core.storage.TempFileManager
import com.app.cardashcam.data.media.VideoRepository
import kotlinx.coroutines.guava.await
import java.io.File

class ServiceCameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) {
    private var cameraProvider: ProcessCameraProvider? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var currentTempFile: File? = null
    private var previewSurface: Preview.SurfaceProvider? = null

    private val storage = StorageManager()
    private val tempFileManager = TempFileManager(context)
    private val repo = VideoRepository(context)

    private var lockNext = false

    suspend fun initialize() {
        try {
            cameraProvider = ProcessCameraProvider.getInstance(context).await()
            
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.FHD))
                .build()
            
            videoCapture = VideoCapture.withOutput(recorder)
            
            bindCamera()
        } catch (e: Exception) {
            Log.e("ServiceCameraController", "Init failed", e)
        }
    }

    fun setPreviewSurface(surfaceProvider: Preview.SurfaceProvider) {
        previewSurface = surfaceProvider
        bindCamera()
    }

    private fun bindCamera() {
        val provider = cameraProvider ?: return
        val surface = previewSurface ?: return
        
        try {
            provider.unbindAll()
            
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(surface)
            }
            
            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                videoCapture
            )
        } catch (e: Exception) {
            Log.e("ServiceCameraController", "Bind failed", e)
        }
    }

    fun startRecording(onStarted: () -> Unit) {
        try {
            if (videoCapture == null || recording != null) return

            if (storage.freeSpaceMB() < 500) {
                repo.deleteOldestUnlocked()
            }

            currentTempFile = tempFileManager.createTempFile()
            val outputOptions = FileOutputOptions.Builder(currentTempFile!!).build()

            recording = videoCapture!!.output
                .prepareRecording(context, outputOptions)
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(context)) { event ->
                    when (event) {
                        is VideoRecordEvent.Start -> {
                            onStarted()
                        }
                        is VideoRecordEvent.Finalize -> {
                            if (event.hasError()) {
                                Log.e("ServiceCameraController", "Recording error: ${event.error}")
                                currentTempFile?.let { tempFileManager.deleteTempFile(it) }
                                currentTempFile = null
                            }
                            recording = null
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("ServiceCameraController", "Start recording failed", e)
            recording = null
            currentTempFile = null
        }
    }

    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    fun saveRecording(): Boolean {
        val tempFile = currentTempFile ?: return false
        val uri = tempFileManager.moveToMediaStore(tempFile, lockNext)
        lockNext = false
        currentTempFile = null
        return uri != null
    }

    fun deleteRecording() {
        currentTempFile?.let {
            tempFileManager.deleteTempFile(it)
        }
        lockNext = false
        currentTempFile = null
    }

    fun lockNextVideo() {
        lockNext = true
    }

    fun isLocked() = lockNext

    fun isRecording() = recording != null

    fun release() {
        recording?.stop()
        recording = null
        cameraProvider?.unbindAll()
        cameraProvider = null
    }
}
