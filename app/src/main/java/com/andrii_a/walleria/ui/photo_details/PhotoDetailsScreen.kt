package com.andrii_a.walleria.ui.photo_details

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.BookmarkRemove
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ZoomInMap
import androidx.compose.material.icons.outlined.ZoomOutMap
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.UiText
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.WLoadingIndicator
import com.andrii_a.walleria.ui.photo_details.components.OverZoomConfig
import com.andrii_a.walleria.ui.photo_details.components.Zoomable
import com.andrii_a.walleria.ui.photo_details.components.rememberZoomableState
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.createdDateTime
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.toast
import com.andrii_a.walleria.ui.util.userFullName
import com.andrii_a.walleria.ui.util.userNickname
import kotlinx.coroutines.launch

@Composable
fun PhotoDetailsScreen(
    state: PhotoDetailsUiState,
    onEvent: (PhotoDetailsEvent) -> Unit,
) {
    when {
        state.isLoading -> {
            LoadingStateContent(
                onNavigateBack = { onEvent(PhotoDetailsEvent.GoBack) }
            )
        }

        !state.isLoading && state.error == null && state.photo != null -> {
            SuccessStateContent(
                state = state,
                onEvent = onEvent,
                modifier = Modifier.fillMaxSize()
            )
        }

        else -> {
            ErrorStateContent(
                onRetry = {
                    val error = state.error as? UiErrorWithRetry
                    error?.onRetry?.invoke()
                },
                onNavigateBack = { onEvent(PhotoDetailsEvent.GoBack) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SuccessStateContent(
    state: PhotoDetailsUiState,
    onEvent: (PhotoDetailsEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val photo = state.photo!!

    val zoomableState = rememberZoomableState(
        minScale = 0.5f,
        maxScale = 6f,
        overZoomConfig = OverZoomConfig(1f, 4f)
    )

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Unused */ }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = state.showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .graphicsLayer {
                        alpha = 1 - zoomableState.dismissDragProgress
                    }
            ) {
                TopBar(
                    onNavigateBack = { onEvent(PhotoDetailsEvent.GoBack) },
                    onOpenInBrowser = { onEvent(PhotoDetailsEvent.OpenInBrowser(photo.links?.html)) },
                    navigateToUserDetails = { onEvent(PhotoDetailsEvent.SelectUser(photo.userNickname)) },
                    currentImageScale = zoomableState.scale,
                    ownerUserFullName = photo.userFullName,
                    dateTimePublished = photo.createdDateTime
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .graphicsLayer {
                        alpha = 1 - zoomableState.dismissDragProgress
                    }
            ) {
                PhotoActionsToolbar(
                    isLikedByLoggedInUser = state.isLikedByLoggedInUser,
                    isPhotoCollected = state.isCollected,
                    zoomIcon = if (zoomableState.scale == 1f) Icons.Outlined.ZoomOutMap else Icons.Outlined.ZoomInMap,
                    onNavigateToCollectPhoto = {
                        if (state.isUserLoggedIn) {
                            onEvent(PhotoDetailsEvent.SelectCollectOption(photo.id))
                        } else {
                            context.toast(stringRes = R.string.login_to_collect_photo)
                            onEvent(PhotoDetailsEvent.RedirectToLogin)
                        }
                    },
                    onLikeButtonClick = {
                        if (state.isUserLoggedIn) {
                            if (state.isLikedByLoggedInUser) {
                                onEvent(PhotoDetailsEvent.DislikePhoto(photo.id))
                            } else {
                                onEvent(PhotoDetailsEvent.LikePhoto(photo.id))
                            }
                        } else {
                            context.toast(stringRes = R.string.login_to_like_photo)
                            onEvent(PhotoDetailsEvent.RedirectToLogin)
                        }
                    },
                    onInfoButtonClick = { onEvent(PhotoDetailsEvent.ShowInfoDialog) },
                    onShareButtonClick = {
                        onEvent(
                            PhotoDetailsEvent.SharePhoto(
                                link = photo.links?.html,
                                description = photo.description
                            )
                        )
                    },
                    onDownloadButtonClick = {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                context.toast(context.getString(R.string.download_started))
                                onEvent(PhotoDetailsEvent.DownloadPhoto(photo))
                            } else {
                                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        } else {
                            context.toast(context.getString(R.string.download_started))
                            onEvent(
                                PhotoDetailsEvent.DownloadPhoto(
                                    photo = photo,
                                    quality = state.photoDownloadQuality
                                )
                            )
                        }
                    },
                    onZoomToFillClick = {
                        scope.launch {
                            zoomableState.animateScaleTo(
                                if (zoomableState.scale >= state.zoomToFillCoefficient) 1f
                                else state.zoomToFillCoefficient
                            )
                        }
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { innerPadding ->
        BoxWithConstraints(modifier = modifier.consumeWindowInsets(innerPadding)) {
            val constraints = this

            Zoomable(
                state = zoomableState,
                enabled = true,
                onTap = { onEvent(PhotoDetailsEvent.ToggleControlsVisibility) },
                dismissGestureEnabled = true,
                onDismiss = {
                    onEvent(PhotoDetailsEvent.GoBack)
                    true
                },
                modifier = Modifier.graphicsLayer {
                    clip = true
                    alpha = 1 - zoomableState.dismissDragProgress
                },
            ) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photo.getUrlByQuality(quality = PhotoQuality.MEDIUM))
                        .size(Size.ORIGINAL)
                        .crossfade(durationMillis = 1000)
                        .build()
                )

                val painterState = painter.state.collectAsStateWithLifecycle()

                AnimatedContent(
                    targetState = painterState,
                    label = "photo_content",
                    transitionSpec = {
                        fadeIn() + scaleIn(animationSpec = tween(400)) togetherWith
                                fadeOut(animationSpec = tween(200))
                    }
                ) { state ->
                    when (state.value) {
                        is AsyncImagePainter.State.Empty -> Unit
                        is AsyncImagePainter.State.Loading -> {
                            WLoadingIndicator(modifier = Modifier.fillMaxSize())
                        }

                        is AsyncImagePainter.State.Error -> {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                ErrorBanner(
                                    message = stringResource(R.string.error_banner_text),
                                    onRetry = painter::restart
                                )
                            }
                        }

                        is AsyncImagePainter.State.Success -> {
                            val size = painter.intrinsicSize

                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier
                                    .aspectRatio(size.width / size.height)
                                    .fillMaxSize()
                            )

                            LaunchedEffect(Unit) {
                                onEvent(
                                    PhotoDetailsEvent.UpdateZoomToFillCoefficient(
                                        imageWidth = size.width,
                                        imageHeight = size.height,
                                        containerWidth = constraints.maxWidth.value,
                                        containerHeight = constraints.maxHeight.value
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.isInfoDialogOpened) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(PhotoDetailsEvent.DismissInfoDialog) },
            sheetState = bottomSheetState
        ) {
            PhotoInfoBottomSheet(
                photo = photo,
                navigateToSearch = { query ->
                    onEvent(PhotoDetailsEvent.SearchByTag(query))
                },
                navigateToCollectionDetails = { id ->
                    onEvent(PhotoDetailsEvent.SelectCollection(id))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingStateContent(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            WLoadingIndicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorStateContent(
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    FilledTonalIconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ErrorBanner(
            onRetry = onRetry,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(
    onNavigateBack: () -> Unit,
    onOpenInBrowser: () -> Unit,
    navigateToUserDetails: () -> Unit,
    currentImageScale: Float = 1f,
    ownerUserFullName: String,
    dateTimePublished: String
) {
    val bgColor by animateColorAsState(targetValue = if (currentImageScale > 1f) Color.Black.copy(alpha = 0.4f) else Color.Transparent)
    val textColor by animateColorAsState(targetValue = if (currentImageScale > 1f) Color.White else MaterialTheme.colorScheme.onSurface)

    val shouldUseDarkIcons = !isSystemInDarkTheme()
    val view = LocalView.current

    DisposableEffect(key1 = currentImageScale) {
        if (currentImageScale > 1f) {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }

        onDispose {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = shouldUseDarkIcons
        }
    }

    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(onClick = navigateToUserDetails)
            ) {
                Text(
                    text = ownerUserFullName,
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor
                )

                Text(
                    text = dateTimePublished,
                    style = MaterialTheme.typography.titleSmall,
                    color = textColor
                )
            }
        },
        navigationIcon = {
            FilledTonalIconButton(
                onClick = onNavigateBack,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_back),
                )
            }
        },
        actions = {
            FilledTonalIconButton(
                onClick = onOpenInBrowser,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = stringResource(id = R.string.open_in_browser),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun PhotoActionsToolbar(
    isLikedByLoggedInUser: Boolean,
    isPhotoCollected: Boolean,
    zoomIcon: ImageVector,
    onNavigateToCollectPhoto: () -> Unit,
    onLikeButtonClick: () -> Unit,
    onInfoButtonClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onDownloadButtonClick: () -> Unit,
    onZoomToFillClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = true,
        modifier = modifier,
        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
        floatingActionButton = {
            FloatingToolbarDefaults.VibrantFloatingActionButton(onClick = onLikeButtonClick) {
                Icon(
                    imageVector = if (isLikedByLoggedInUser) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = null
                )
            }
        }
    ) {
        IconButton(onClick = onInfoButtonClick) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
            )
        }

        IconButton(onClick = onNavigateToCollectPhoto) {
            Icon(
                imageVector = if (isPhotoCollected) {
                    Icons.Filled.BookmarkRemove
                } else {
                    Icons.Filled.BookmarkAdd
                },
                contentDescription = null
            )
        }

        IconButton(onClick = onDownloadButtonClick) {
            Icon(
                imageVector = Icons.Filled.CloudDownload,
                contentDescription = null,
            )
        }

        IconButton(onClick = onShareButtonClick) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
            )
        }

        IconButton(onClick = onZoomToFillClick) {
            Icon(
                imageVector = zoomIcon,
                contentDescription = null,
            )
        }
    }
}

private class PhotoDetailsUiStateProvider : PreviewParameterProvider<PhotoDetailsUiState> {
    private val user = User(
        id = "",
        username = "ABC",
        firstName = "John",
        lastName = "Smith",
        bio = null,
        location = null,
        totalLikes = 0,
        totalPhotos = 0,
        totalCollections = 0,
        followersCount = 0,
        followingCount = 0,
        downloads = 0,
        profileImage = null,
        social = null,
        tags = null,
        photos = null
    )

    private val photo = Photo(
        id = "",
        width = 4000,
        height = 3000,
        color = "#E0E0E0",
        createdAt = "2023-05-03T11:00:28Z",
        blurHash = "LFC\$yHwc8^\$yIAS$%M%00KxukYIp",
        views = 200,
        downloads = 200,
        likes = 10,
        likedByUser = false,
        description = "",
        exif = null,
        location = null,
        tags = null,
        relatedCollections = null,
        currentUserCollections = null,
        sponsorship = null,
        urls = PhotoUrls("", "", "", "", ""),
        links = null,
        user = user
    )

    override val values = sequenceOf(
        PhotoDetailsUiState(isLoading = true),
        PhotoDetailsUiState(error = UiErrorWithRetry(reason = UiText.DynamicString("ABC"))),
        PhotoDetailsUiState(
            photo = photo,
            isLikedByLoggedInUser = true,
        )
    )
}

@PreviewLightDark
@Composable
fun PhotoDetailsScreenPreview(
    @PreviewParameter(PhotoDetailsUiStateProvider::class)
    state: PhotoDetailsUiState
) {
    WalleriaTheme {
        Surface {
            PhotoDetailsScreen(
                state = state,
                onEvent = {}
            )
        }
    }
}

