# CarDashCam - Phase 2 Improvements Roadmap

## 🎯 Current Status: Production Ready ✅

The app is now stable and production-ready. This document outlines optional improvements for Phase 2.

---

## 🏗️ ARCHITECTURE ENHANCEMENTS

### 1. Dependency Injection (Priority: HIGH)
**Why**: Remove manual dependency creation, improve testability.

**Implementation**: Add Hilt
```kotlin
// build.gradle.kts
plugins {
    id("com.google.dagger.hilt.android")
}

// Module example
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.get(context)
    }
}

// ViewModel example
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel()
```

**Benefits**:
- No manual repository creation
- Easy testing with fake implementations
- Singleton management
- Scoped dependencies

---

### 2. Domain Layer Use Cases (Priority: MEDIUM)
**Why**: Separate business logic from ViewModels.

**Structure**:
```
domain/
├── model/
├── repository/          # Interfaces
└── usecase/
    ├── auth/
    │   ├── LoginUseCase.kt
    │   └── RegisterUseCase.kt
    ├── video/
    │   ├── GetVideosUseCase.kt
    │   └── DeleteVideoUseCase.kt
    └── camera/
        └── StartRecordingUseCase.kt
```

**Example**:
```kotlin
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) {
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        return try {
            val success = authRepository.login(email, password)
            if (success) {
                sessionManager.saveLogin(email)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

### 3. Repository Interfaces (Priority: MEDIUM)
**Why**: Enable easy testing and swapping implementations.

```kotlin
// domain/repository/AuthRepository.kt
interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(name: String, email: String, password: String): Boolean
}

// data/repository/AuthRepositoryImpl.kt
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {
    override suspend fun login(email: String, password: String): Boolean {
        // implementation
    }
}
```

---

## 🔐 SECURITY IMPROVEMENTS

### 4. Password Hashing (Priority: HIGH)
**Why**: Never store plain text passwords.

**Implementation**:
```kotlin
// Add dependency
implementation("androidx.security:security-crypto:1.1.0-alpha06")

// Hash password
object PasswordHasher {
    fun hash(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hash = md.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
    
    fun verify(password: String, hash: String): Boolean {
        return hash(password) == hash
    }
}

// Usage in repository
suspend fun register(name: String, email: String, password: String): Boolean {
    val hashedPassword = PasswordHasher.hash(password)
    dao.insert(UserEntity(name = name, email = email, password = hashedPassword))
}
```

---

### 5. Encrypted SharedPreferences (Priority: MEDIUM)
**Why**: Secure session data.

```kotlin
val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

val encryptedPrefs = EncryptedSharedPreferences.create(
    context,
    "secure_session",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

---

## 🎨 UI/UX ENHANCEMENTS

### 6. Professional Camera UI (Priority: HIGH)
**Current**: Basic overlay buttons
**Target**: Professional dashcam interface

**Improvements**:
```kotlin
// Add to CameraScreen
- Larger, more visible recording indicator
- Better speed HUD (digital speedometer style)
- GPS coordinates display
- Date/time overlay
- Battery indicator
- Storage indicator
- Recording quality indicator
- Grid overlay option
- Night mode toggle
```

**Design**:
- Use glassmorphism for all overlays
- Add subtle animations
- Improve button sizes for driving (min 48dp touch target)
- Add haptic feedback

---

### 7. Settings Screen (Priority: MEDIUM)
**Features**:
```kotlin
@Composable
fun SettingsScreen() {
    // Video Quality
    - Resolution (720p, 1080p, 4K)
    - Frame rate (30fps, 60fps)
    - Bitrate
    
    // Storage
    - Loop recording on/off
    - Storage threshold (500MB, 1GB, 2GB)
    - Auto-delete unlocked videos
    
    // Camera
    - Grid overlay
    - Stabilization
    - Auto-focus
    
    // Crash Detection
    - Sensitivity (2G, 3G, 4G)
    - Auto-lock on crash
    
    // Account
    - Change password
    - Logout
    - Delete account
}
```

---

### 8. Improved Gallery UI (Priority: MEDIUM)
**Enhancements**:
```kotlin
// Add to GalleryScreen
- Video thumbnails (using Coil)
- Grid view option
- Sort options (date, size, name)
- Filter (locked/unlocked)
- Search by date
- Batch delete
- Share video
- Video duration display
- Preview on long press
```

**Implementation**:
```kotlin
// Add Coil for thumbnails
implementation("io.coil-kt:coil-compose:2.5.0")
implementation("io.coil-kt:coil-video:2.5.0")

AsyncImage(
    model = ImageRequest.Builder(context)
        .data(video.uri)
        .videoFrameMillis(1000)
        .build(),
    contentDescription = null
)
```

---

### 9. Animations & Transitions (Priority: LOW)
**Add**:
```kotlin
// Screen transitions
AnimatedContent(targetState = currentScreen) { screen ->
    when (screen) {
        // screens
    }
}

// Recording pulse animation
val infiniteTransition = rememberInfiniteTransition()
val alpha by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(1000),
        repeatMode = RepeatMode.Reverse
    )
)

// Shimmer loading effect for gallery
```

---

## 🚀 FEATURE ADDITIONS

### 10. Video Trimming (Priority: MEDIUM)
**Why**: Allow users to trim videos before sharing.

**Library**: FFmpeg or Media3 Transformer
```kotlin
implementation("androidx.media3:media3-transformer:1.3.0")
```

---

### 11. GPS Integration (Priority: MEDIUM)
**Why**: Record location data with videos.

**Implementation**:
```kotlin
// Add permission
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>

// Create LocationManager
class LocationTracker @Inject constructor(
    private val context: Context
) {
    fun getCurrentLocation(): Location? {
        // implementation
    }
}

// Overlay on video
Text("${location.latitude}, ${location.longitude}")
Text("${location.speed} km/h")  // Real speed instead of simulator
```

---

### 12. Cloud Backup (Optional - Priority: LOW)
**Note**: Requirements say offline-first, but optional cloud backup could be useful.

**Implementation**:
```kotlin
// Only if user opts in
- Google Drive integration
- Dropbox integration
- Auto-upload locked videos only
- WiFi-only option
```

---

## 🧪 TESTING

### 13. Unit Tests (Priority: HIGH)
**Coverage Target**: 80%+

```kotlin
// Example: AuthViewModelTest
@Test
fun `login with valid credentials should succeed`() = runTest {
    // Given
    val email = "test@test.com"
    val password = "password123"
    coEvery { authRepository.login(email, password) } returns true
    
    // When
    viewModel.login(email, password)
    
    // Then
    assertTrue(viewModel.loginSuccess.value)
}
```

**Test**:
- ViewModels
- Repositories
- Use Cases
- Utilities

---

### 14. UI Tests (Priority: MEDIUM)
```kotlin
@Test
fun loginFlow_withValidCredentials_navigatesToCamera() {
    composeTestRule.setContent {
        AppNavHost()
    }
    
    composeTestRule.onNodeWithText("Email").performTextInput("test@test.com")
    composeTestRule.onNodeWithText("Password").performTextInput("password123")
    composeTestRule.onNodeWithText("Login").performClick()
    
    composeTestRule.onNodeWithText("Gallery").assertExists()
}
```

---

### 15. Integration Tests (Priority: MEDIUM)
**Test**:
- Database operations
- File operations
- Camera recording
- Video playback

---

## 📊 ANALYTICS & MONITORING

### 16. Crash Reporting (Priority: HIGH)
**Options**:
- Firebase Crashlytics (if cloud allowed)
- Sentry
- Bugsnag

**Benefits**:
- Track crashes in production
- Monitor app health
- Fix issues proactively

---

### 17. Performance Monitoring (Priority: MEDIUM)
**Track**:
- App startup time
- Camera initialization time
- Video encoding performance
- Memory usage
- Battery consumption

---

## 🎯 ACCESSIBILITY

### 18. Accessibility Improvements (Priority: MEDIUM)
```kotlin
// Add content descriptions
Icon(
    Icons.Default.Videocam,
    contentDescription = "Start recording"
)

// Add semantic properties
Text(
    "Recording",
    modifier = Modifier.semantics {
        contentDescription = "Currently recording video"
    }
)

// Support TalkBack
// Support large text
// Support high contrast
```

---

## 📱 PLATFORM FEATURES

### 19. Widgets (Priority: LOW)
**Quick Actions**:
- Start recording widget
- View last recording widget

---

### 20. Shortcuts (Priority: LOW)
```kotlin
// AndroidManifest.xml
<activity>
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
    </intent-filter>
</activity>

// Dynamic shortcuts
val shortcut = ShortcutInfo.Builder(context, "start_recording")
    .setShortLabel("Start Recording")
    .setIcon(Icon.createWithResource(context, R.drawable.ic_record))
    .setIntent(intent)
    .build()
```

---

## 🔧 DEVELOPER EXPERIENCE

### 21. CI/CD Pipeline (Priority: MEDIUM)
**Setup**:
```yaml
# .github/workflows/android.yml
name: Android CI

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
      - name: Build with Gradle
        run: ./gradlew build
      - name: Run tests
        run: ./gradlew test
```

---

### 22. Code Quality Tools (Priority: MEDIUM)
**Add**:
```kotlin
// Detekt for static analysis
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
}

// Ktlint for formatting
plugins {
    id("org.jlleitschuh.gradle.ktlint") version "11.5.0"
}
```

---

## 📈 PRIORITY MATRIX

### Must Have (Phase 2A)
1. ✅ Password Hashing
2. ✅ Professional Camera UI
3. ✅ Unit Tests
4. ✅ Dependency Injection

### Should Have (Phase 2B)
5. ✅ Settings Screen
6. ✅ Improved Gallery UI
7. ✅ GPS Integration
8. ✅ Domain Layer Use Cases

### Nice to Have (Phase 2C)
9. ✅ Video Trimming
10. ✅ Animations
11. ✅ Widgets
12. ✅ Cloud Backup (optional)

---

## 📝 IMPLEMENTATION ORDER

### Sprint 1 (2 weeks)
- Password hashing
- Dependency Injection setup
- Unit tests for ViewModels

### Sprint 2 (2 weeks)
- Professional Camera UI redesign
- Settings screen
- GPS integration

### Sprint 3 (2 weeks)
- Improved Gallery UI
- Domain layer refactor
- UI tests

### Sprint 4 (1 week)
- Animations
- Accessibility
- Polish

---

## 🎓 LEARNING RESOURCES

### Recommended Reading
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [CameraX Documentation](https://developer.android.com/training/camerax)
- [Jetpack Compose Best Practices](https://developer.android.com/jetpack/compose/best-practices)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)

---

**Note**: All Phase 2 improvements are optional. The app is production-ready as-is.

**Last Updated**: 2024
