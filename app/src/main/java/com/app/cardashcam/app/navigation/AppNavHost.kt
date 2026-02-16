package com.app.cardashcam.app.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.app.cardashcam.feature_auth.ui.LoginScreen
import com.app.cardashcam.feature_auth.ui.RegisterScreen
import com.app.cardashcam.feature_camera.ui.CameraScreen
import com.app.cardashcam.feature_gallery.ui.GalleryScreen
import com.app.cardashcam.feature_player.ui.PlayerScreen
import com.app.cardashcam.feature_splash.SplashScreen

@Composable
fun AppNavHost() {

    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.SPLASH
    ) {

        composable(
            route = Routes.SPLASH,
            enterTransition = { NavigationTransitions.enterTransition() },
            exitTransition = { NavigationTransitions.exitTransition() }
        ) {
            SplashScreen { isLoggedIn ->
                val destination = if (isLoggedIn) Routes.CAMERA else Routes.LOGIN
                nav.navigate(destination) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            }
        }

        composable(
            route = Routes.LOGIN,
            enterTransition = { NavigationTransitions.enterTransition() },
            exitTransition = { NavigationTransitions.exitTransition() },
            popEnterTransition = { NavigationTransitions.popEnterTransition() },
            popExitTransition = { NavigationTransitions.popExitTransition() }
        ) {
            LoginScreen(
                onLoginSuccess = {
                    nav.navigate(Routes.CAMERA) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onRegister = {
                    nav.navigate(Routes.REGISTER)
                }
            )
        }

        composable(
            route = Routes.REGISTER,
            enterTransition = { NavigationTransitions.enterTransition() },
            exitTransition = { NavigationTransitions.exitTransition() },
            popEnterTransition = { NavigationTransitions.popEnterTransition() },
            popExitTransition = { NavigationTransitions.popExitTransition() }
        ) {
            RegisterScreen(
                onBack = { nav.popBackStack() }
            )
        }

        composable(
            route = Routes.CAMERA,
            enterTransition = { NavigationTransitions.enterTransition() },
            exitTransition = { NavigationTransitions.exitTransition() },
            popEnterTransition = { NavigationTransitions.popEnterTransition() },
            popExitTransition = { NavigationTransitions.popExitTransition() }
        ) {
            CameraScreen(
                openGallery = { nav.navigate(Routes.GALLERY) }
            )
        }

        composable(
            route = Routes.GALLERY,
            enterTransition = { NavigationTransitions.enterTransition() },
            exitTransition = { NavigationTransitions.exitTransition() },
            popEnterTransition = { NavigationTransitions.popEnterTransition() },
            popExitTransition = { NavigationTransitions.popExitTransition() }
        ) {
            GalleryScreen(
                onBack = { nav.popBackStack() },
                onVideoClick = { uri ->
                    val encoded = Uri.encode(uri.toString())
                    nav.navigate("${Routes.PLAYER}/$encoded")
                }
            )
        }

        composable(
            route = "${Routes.PLAYER}/{uri}",
            enterTransition = { NavigationTransitions.enterTransition() },
            exitTransition = { NavigationTransitions.exitTransition() },
            popEnterTransition = { NavigationTransitions.popEnterTransition() },
            popExitTransition = { NavigationTransitions.popExitTransition() }
        ) { backStack ->
            val uriString = backStack.arguments?.getString("uri")
            if (uriString != null) {
                val uri = Uri.parse(uriString)
                PlayerScreen(
                    uri = uri,
                    onBack = { nav.popBackStack() }
                )
            }
        }
    }
}
