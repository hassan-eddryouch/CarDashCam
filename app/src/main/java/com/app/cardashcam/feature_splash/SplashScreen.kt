package com.app.cardashcam.feature_splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import com.app.cardashcam.core.ui.components.AnimatedGradientBackground
import com.app.cardashcam.data.session.SessionManager
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigate: (isLoggedIn: Boolean) -> Unit) {

    val context = LocalContext.current
    val session = remember { SessionManager(context) }

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "alpha"
    )

    LaunchedEffect(Unit) {
        delay(1800)
        onNavigate(session.isLogged())
    }

    AnimatedGradientBackground(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "CarDashCam",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha)
            )
        }
    }
}
