package com.app.cardashcam.app.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset

object NavigationTransitions {
    
    private const val DURATION = 400
    
    fun enterTransition(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(DURATION)
        ) + fadeIn(animationSpec = tween(DURATION))
    }
    
    fun exitTransition(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { -it / 3 },
            animationSpec = tween(DURATION)
        ) + fadeOut(animationSpec = tween(DURATION))
    }
    
    fun popEnterTransition(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { -it / 3 },
            animationSpec = tween(DURATION)
        ) + fadeIn(animationSpec = tween(DURATION))
    }
    
    fun popExitTransition(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(DURATION)
        ) + fadeOut(animationSpec = tween(DURATION))
    }
}
