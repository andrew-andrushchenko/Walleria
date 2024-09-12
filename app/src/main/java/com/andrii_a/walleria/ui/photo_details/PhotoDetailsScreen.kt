package com.andrii_a.walleria.ui.photo_details

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ZoomInMap
import androidx.compose.material.icons.outlined.ZoomOutMap
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.photo.PhotoUrls
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.UiText
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.photo_details.components.OverZoomConfig
import com.andrii_a.walleria.ui.photo_details.components.UserRow
import com.andrii_a.walleria.ui.photo_details.components.Zoomable
import com.andrii_a.walleria.ui.photo_details.components.rememberZoomableState
import com.andrii_a.walleria.ui.theme.PhotoDetailsActionButtonContainerColor
import com.andrii_a.walleria.ui.theme.PhotoDetailsActionButtonContentColor
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.getProfileImageUrlOrEmpty
import com.andrii_a.walleria.ui.util.getUrlByQuality
import com.andrii_a.walleria.ui.util.toast
import com.andrii_a.walleria.ui.util.userFullName
import com.andrii_a.walleria.ui.util.userNickname
import kotlinx.coroutines.launch
import kotlin.math.max

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

@JvmInline
value class LikeCount(val value: Long)

@OptIn(ExperimentalMaterial3Api::class)
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

    BoxWithConstraints(modifier = modifier.background(Color.Black)) {
        val constraints = this

        var areControlsVisible by rememberSaveable { mutableStateOf(true) }
        var zoomToFillCoefficient by rememberSaveable { mutableFloatStateOf(1f) }

        val zoomableState = rememberZoomableState(
            minScale = 0.5f,
            maxScale = 6f,
            overZoomConfig = OverZoomConfig(1f, 4f)
        )

        Zoomable(
            state = zoomableState,
            enabled = true,
            onTap = { areControlsVisible = !areControlsVisible },
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

            if (painter.state is AsyncImagePainter.State.Success) {
                val size = painter.intrinsicSize

                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(size.width / size.height)
                        .fillMaxSize()
                )

                zoomToFillCoefficient = getZoomToFillScaleCoefficient(
                    imageWidth = size.width,
                    imageHeight = size.height,
                    containerWidth = constraints.maxWidth.value,
                    containerHeight = constraints.maxHeight.value
                )
            }
        }

        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    alpha = 1 - zoomableState.dismissDragProgress
                }
        ) {
            TopBar(
                onNavigateBack = { onEvent(PhotoDetailsEvent.GoBack) },
                onOpenInBrowser = { onEvent(PhotoDetailsEvent.OpenInBrowser(photo.links?.html)) }
            )
        }

        val launcher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { /* Unused */ }

        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    alpha = 1 - zoomableState.dismissDragProgress
                }
        ) {
            BottomControls(
                likes = photo.likes,
                photoOwner = photo.user,
                isPhotoLiked = state.isLiked,
                isPhotoCollected = state.isCollected,
                zoomIcon = if (zoomableState.scale == 1f) Icons.Outlined.ZoomOutMap else Icons.Outlined.ZoomInMap,
                onNavigateToUserDetails = {
                    onEvent(PhotoDetailsEvent.SelectUser(photo.userNickname))
                },
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
                        if (state.isLiked) {
                            onEvent(PhotoDetailsEvent.DislikePhoto(photo.id))
                            LikeCount(value = photo.likes)
                        } else {
                            onEvent(PhotoDetailsEvent.LikePhoto(photo.id))
                            LikeCount(value = photo.likes + 1)
                        }
                    } else {
                        context.toast(stringRes = R.string.login_to_like_photo)
                        onEvent(PhotoDetailsEvent.RedirectToLogin)
                        null
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
                            if (zoomableState.scale >= zoomToFillCoefficient) 1f
                            else zoomToFillCoefficient
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Black.copy(alpha = 0.5f))
                    .navigationBarsPadding()
                    .padding(12.dp)
            )
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
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
    onOpenInBrowser: () -> Unit
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_back),
                )
            }
        },
        actions = {
            IconButton(onClick = onOpenInBrowser) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = stringResource(id = R.string.open_in_browser),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black.copy(alpha = 0.3f),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@Composable
private fun BottomControls(
    likes: Long,
    photoOwner: User?,
    isPhotoLiked: Boolean,
    isPhotoCollected: Boolean,
    zoomIcon: ImageVector,
    onNavigateToUserDetails: () -> Unit,
    onNavigateToCollectPhoto: () -> Unit,
    onLikeButtonClick: () -> LikeCount?,
    onInfoButtonClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onDownloadButtonClick: () -> Unit,
    onZoomToFillClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier) {
        val (likeButton, collectButton, userRow,
            zoomToFillButton, infoButton, shareButton, downloadButton) = createRefs()

        var photoLikes by rememberSaveable { mutableLongStateOf(likes) }

        ExtendedFloatingActionButton(
            text = {
                Text(text = photoLikes.abbreviatedNumberString)
            },
            icon = {
                Icon(
                    imageVector = if (isPhotoLiked) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = null
                )
            },
            onClick = {
                val likeCount = onLikeButtonClick()
                likeCount?.let { photoLikes = it.value }
            },
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            containerColor = PhotoDetailsActionButtonContainerColor,
            contentColor = PhotoDetailsActionButtonContentColor,
            modifier = Modifier.constrainAs(likeButton) {
                top.linkTo(parent.top, 4.dp)
                start.linkTo(parent.start, 8.dp)
            }
        )

        ExtendedFloatingActionButton(
            text = {
                Text(
                    text = stringResource(
                        id = if (isPhotoCollected) R.string.drop
                        else R.string.collect
                    )
                )
            },
            icon = {
                Icon(
                    imageVector = if (isPhotoCollected) {
                        Icons.Filled.Bookmark
                    } else {
                        Icons.Outlined.BookmarkBorder
                    },
                    contentDescription = null
                )
            },
            onClick = onNavigateToCollectPhoto,
            elevation = FloatingActionButtonDefaults.elevation(0.dp),
            containerColor = PhotoDetailsActionButtonContainerColor,
            contentColor = PhotoDetailsActionButtonContentColor,
            modifier = Modifier.constrainAs(collectButton) {
                top.linkTo(likeButton.top)
                start.linkTo(likeButton.end, 4.dp)
                bottom.linkTo(likeButton.bottom)
            }
        )

        IconButton(
            onClick = onZoomToFillClick,
            modifier = Modifier.constrainAs(zoomToFillButton) {
                top.linkTo(collectButton.top)
                bottom.linkTo(collectButton.bottom)
                end.linkTo(parent.end)
            }
        ) {
            Icon(
                imageVector = zoomIcon,
                contentDescription = null,
                tint = Color.White
            )
        }

        UserRow(
            userProfileImageUrl = photoOwner?.getProfileImageUrlOrEmpty().orEmpty(),
            username = photoOwner?.userFullName.orEmpty(),
            onUserClick = onNavigateToUserDetails,
            modifier = Modifier.constrainAs(userRow) {
                top.linkTo(likeButton.bottom, 4.dp)
                bottom.linkTo(parent.bottom, 4.dp)
                start.linkTo(parent.start)
                end.linkTo(infoButton.start)
                width = Dimension.fillToConstraints
            }
        )

        IconButton(
            onClick = onInfoButtonClick,
            modifier = Modifier.constrainAs(infoButton) {
                top.linkTo(userRow.top)
                bottom.linkTo(userRow.bottom)
                end.linkTo(shareButton.start, 4.dp)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                tint = Color.White
            )
        }

        IconButton(
            onClick = onShareButtonClick,
            modifier = Modifier.constrainAs(shareButton) {
                top.linkTo(infoButton.top)
                bottom.linkTo(infoButton.bottom)
                end.linkTo(downloadButton.start, 4.dp)
            }
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = Color.White
            )
        }

        IconButton(
            onClick = onDownloadButtonClick,
            modifier = Modifier.constrainAs(downloadButton) {
                end.linkTo(parent.end)
                bottom.linkTo(shareButton.bottom)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.FileDownload,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
private fun getZoomToFillScaleCoefficient(
    imageWidth: Float,
    imageHeight: Float,
    containerWidth: Float,
    containerHeight: Float
): Float {
    val widthRatio = imageWidth / imageHeight
    val height = containerWidth / widthRatio
    val zoomScaleH = containerHeight / height

    val heightRatio = imageHeight / imageWidth
    val width = containerHeight / heightRatio
    val zoomScaleW = containerWidth / width

    return max(zoomScaleW, zoomScaleH)
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
        blurHash = "LFC\$yHwc8^\$yIAS\$%M%00KxukYIp",
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
            isLiked = true,
        )
    )
}

@Preview
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

