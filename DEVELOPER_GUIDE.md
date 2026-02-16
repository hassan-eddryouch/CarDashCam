# CarDashCam - Developer Quick Reference

## 🚀 Quick Start

### Build & Run
```bash
./gradlew assembleDebug
./gradlew installDebug
```

### Test on Device
1. Enable Developer Options
2. Enable USB Debugging
3. Connect device
4. Run from Android Studio

---

## 📁 Project Structure

```
app/src/main/java/com/app/cardashcam/
├── app/
│   └── navigation/          # Navigation graph
├── core/
│   ├── storage/            # Storage utilities
│   └── ui/                 # Reusable UI components
│       ├── components/     # Buttons, Cards, etc.
│       └── theme/          # Colors, Typography, Theme
├── data/
│   ├── local/              # Room database
│   ├── media/              # Video repository
│   ├── repository/         # Auth repository
│   └── session/            # Session management
├── domain/
│   └── model/              # Domain models
├── feature_auth/           # Login & Register
│   ├── ui/
│   └── viewmodel/
├── feature_camera/         # DashCam recording
│   ├── camera/
│   ├── sensors/
│   ├── service/
│   ├── state/
│   └── ui/
├── feature_gallery/        # Video list
│   ├── ui/
│   └── viewmodel/
├── feature_player/         # Video playback
│   └── ui/
├── feature_splash/         # Splash screen
└── MainActivity.kt
```

---

## 🔑 Key Components

### ViewModels
All ViewModels now use:
- `StateFlow` for state management
- `viewModelScope` for coroutines
- Proper error handling

```kotlin
// Example
private val _state = MutableStateFlow<State>(State.Initial)
val state = _state.asStateFlow()

fun doSomething() {
    viewModelScope.launch {
        try {
            // async work
        } catch (e: Exception) {
            // handle error
        }
    }
}
```

### Composables
Collect StateFlow properly:
```kotlin
val state by viewModel.state.collectAsStateWithLifecycle()
```

### Repository Pattern
All data operations use coroutines:
```kotlin
suspend fun getData() = withContext(Dispatchers.IO) {
    // blocking operation
}
```

---

## 🎨 UI Theme

### Colors
- `Night` - Background (0xFF05070A)
- `NeonBlue` - Primary (0xFF1E88FF)
- `RecordingRed` - Recording indicator (0xFFFF3B30)
- `TextPrimary` - Main text (0xFFEAF2FF)

### Components
- `GlassCard` - Glassmorphism container
- `NeonButton` - Primary button
- `NeonTextField` - Input field

---

## 📸 Camera Features

### Recording
- Auto-start on camera ready
- Loop recording (deletes oldest when storage < 500MB)
- Lock important videos (manual + crash detection)
- Foreground service for background recording

### File Format
```
video_YYYYMMDD_HHMMSS.mp4
video_YYYYMMDD_HHMMSS_LOCK.mp4  (locked)
```

### Storage Location
```
Movies/CarDashCam/
```

---

## 🔐 Permissions

### Required
- CAMERA
- RECORD_AUDIO
- READ_MEDIA_VIDEO (Android 13+)
- READ_EXTERNAL_STORAGE (Android 10-12)
- WRITE_EXTERNAL_STORAGE (Android 9-)
- FOREGROUND_SERVICE
- POST_NOTIFICATIONS

### Handling
```kotlin
val permissionState = rememberMultiplePermissionsState(
    listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
)

LaunchedEffect(Unit) {
    permissionState.launchMultiplePermissionRequest()
}

if (!permissionState.allPermissionsGranted) {
    // Show permission required UI
}
```

---

## 💾 Database

### User Entity
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String  // TODO: Hash in production
)
```

### Access
```kotlin
val db = AppDatabase.get(context)
val userDao = db.userDao()
```

---

## 🔄 Session Management

```kotlin
val session = SessionManager(context)

// Save login
session.saveLogin(email)

// Check if logged in
if (session.isLogged()) { }

// Get current user
val email = session.currentUser()

// Logout
session.logout()
```

---

## 🎥 Video Operations

### Get Videos
```kotlin
val repo = VideoRepository(context)
val videos = repo.getVideos()  // Returns List<VideoItem>
```

### Delete Video
```kotlin
repo.deleteVideo(uri)
```

### Delete Oldest Unlocked
```kotlin
repo.deleteOldestUnlocked()  // For loop recording
```

---

## 🚨 Crash Detection

```kotlin
val crashDetector = CrashDetector(context) {
    // Called when crash detected (> 3G)
    controller.lockNextVideo()
}

DisposableEffect(Unit) {
    crashDetector.start()
    onDispose { crashDetector.stop() }
}
```

---

## 🐛 Debugging

### Logs
```kotlin
Log.d("TAG", "Debug message")
Log.e("TAG", "Error message", exception)
```

### Common Issues

**Camera not starting**
- Check permissions granted
- Check camera not in use by another app
- Check logs for CameraX errors

**Recording not saving**
- Check storage permissions
- Check available storage space
- Check MediaStore write permissions

**App crashes on navigation**
- Check null safety in navigation arguments
- Check ViewModel lifecycle

---

## 📦 Dependencies

### Core
- Kotlin 2.0.21
- Compose BOM 2025.02.00
- Material3

### Architecture
- Navigation Compose 2.8.0
- Lifecycle 2.8.4
- Room 2.7.0
- Coroutines 1.9.0

### Camera
- CameraX 1.4.1
- ExoPlayer (Media3) 1.3.0

### Utilities
- Accompanist Permissions 0.36.0

---

## ✅ Testing Checklist

### Before Release
- [ ] Test on Android 8, 10, 13, 14
- [ ] Test all permissions scenarios
- [ ] Test low storage scenario
- [ ] Test crash detection
- [ ] Test video playback
- [ ] Test session persistence
- [ ] Test registration/login
- [ ] Test loop recording
- [ ] Test locked videos
- [ ] Memory leak testing (LeakCanary)

---

## 🔧 Build Variants

### Debug
```bash
./gradlew assembleDebug
```

### Release
```bash
./gradlew assembleRelease
```

**Note**: Add signing config for release builds.

---

## 📞 Support

For issues or questions:
1. Check logs first
2. Review REFACTOR_SUMMARY.md
3. Check Android documentation
4. Review CameraX documentation

---

**Last Updated**: 2024
**Min SDK**: 26 (Android 8.0)
**Target SDK**: 34 (Android 14)
