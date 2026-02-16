package com.app.cardashcam.feature_camera.camera

import android.content.Context
import android.net.Uri
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

class DashCamController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val previewView: PreviewView
) {

    private var recording: Recording? = null
    private var videoCapture: VideoCapture<Recorder>? = null
    private var currentTempFile: File? = null

    private val storage = StorageManager()
    private val tempFileManager = TempFileManager(context)
    private val repo = VideoRepository(context)

    private var lockNext = false
    fun lockNextVideo() { lockNext = true }
    fun isLocked() = lockNext

    suspend fun startCamera() {
        try {
            val provider = ProcessCameraProvider.getInstance(context).await()

            val preview = Preview.Builder().build().apply {
                surfaceProvider = previewView.surfaceProvider
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.FHD))
                .build()

            videoCapture = VideoCapture.withOutput(recorder)

            provider.unbindAll()

            provider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                videoCapture
            )
        } catch (e: Exception) {
            Log.e("DashCamController", "Camera init failed", e)
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
                                Log.e("DashCamController", "Recording error: ${event.error}")
                                currentTempFile?.let { tempFileManager.deleteTempFile(it) }
                                currentTempFile = null
                            }
                            recording = null
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e("DashCamController", "Start recording failed", e)
            recording = null
            currentTempFile = null
        }
    }

    fun stopRecording() {
        recording?.stop()
        recording = null
    }

    fun saveRecording(): Uri? {
        val tempFile = currentTempFile ?: return null
        val uri = tempFileManager.moveToMediaStore(tempFile, lockNext)
        lockNext = false
        currentTempFile = null
        return uri
    }

    fun deleteRecording() {
        currentTempFile?.let {
            tempFileManager.deleteTempFile(it)
        }
        lockNext = false
        currentTempFile = null
    }

    fun isRecording(): Boolean = recording != null
}
