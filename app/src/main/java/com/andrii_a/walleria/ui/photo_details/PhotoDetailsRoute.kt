package com.andrii_a.walleria.ui.photo_details

import android.app.Activity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.andrii_a.walleria.ui.login.LoginActivity
import com.andrii_a.walleria.ui.navigation.Screen
import com.andrii_a.walleria.ui.util.InterScreenCommunicationKeys
import com.andrii_a.walleria.ui.util.collectAsOneTimeEvents
import com.andrii_a.walleria.ui.util.openLinkInBrowser
import com.andrii_a.walleria.ui.util.sharePhoto
import com.andrii_a.walleria.ui.util.startActivity

fun NavGraphBuilder.photoDetailsRoute(navController: NavController) {
    composable<Screen.PhotoDetails>(
        enterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        },
        popEnterTransition = {
            fadeIn(
                animationSpec = tween(300, easing = LinearEasing)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = spring(stiffness = Spring.StiffnessMedium)
            )
        },
    ) {
        val viewModel: PhotoDetailsViewModel = hiltViewModel()

        val state by viewModel.state.collectAsStateWithLifecycle()

        val shouldUseDarkIcons = !isSystemInDarkTheme()
        val view = LocalView.current

        DisposableEffect(key1 = state) {
            when {
                state.isLoading || state.error != null -> Unit
                else -> {
                    val window = (view.context as Activity).window
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                        false
                }
            }

            onDispose {
                when {
                    state.isLoading || state.error != null -> Unit
                    else -> {
                        val window = (view.context as Activity).window
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                            shouldUseDarkIcons
                    }
                }
            }
        }

        val collectResult = navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow(
                InterScreenCommunicationKeys.COLLECT_SCREEN_RESULT_KEY,
                state.isCollected
            )
            ?.collectAsStateWithLifecycle()

        LaunchedEffect(key1 = collectResult?.value) {
            viewModel.onEvent(
                if (collectResult?.value == true) {
                    PhotoDetailsEvent.MakeCollected
                } else {
                    PhotoDetailsEvent.MakeDropped
                }
            )
        }

        val context = LocalContext.current
        viewModel.navigationEventsChannelFlow.collectAsOneTimeEvents { event ->
            when (event) {
                is PhotoDetailsNavigationEvent.NavigateBack -> {
                    navController.navigateUp()
                }

                is PhotoDetailsNavigationEvent.NavigateToCollectPhoto -> {
                    navController.navigate(Screen.CollectPhoto(event.photoId))
                }

                is PhotoDetailsNavigationEvent.NavigateToCollectionDetails -> {
                    navController.navigate(Screen.CollectionDetails(event.collectionId))
                }

                is PhotoDetailsNavigationEvent.NavigateToSearch -> {
                    navController.navigate(Screen.Search(event.query))
                }

                is PhotoDetailsNavigationEvent.NavigateToUserDetails -> {
                    navController.navigate(Screen.UserDetails(event.userNickname))
                }

                is PhotoDetailsNavigationEvent.NavigateToChromeCustomTab -> {
                    context.openLinkInBrowser(event.url)
                }

                is PhotoDetailsNavigationEvent.NavigateToLogin -> {
                    context.startActivity(LoginActivity::class.java)
                }

                is PhotoDetailsNavigationEvent.NavigateToShareDialog -> {
                    context.sharePhoto(
                        photoLink = event.link,
                        photoDescription = event.description
                    )
                }
            }
        }

        PhotoDetailsScreen(
            state = state,
            onEvent = viewModel::onEvent
        )
    }
}

