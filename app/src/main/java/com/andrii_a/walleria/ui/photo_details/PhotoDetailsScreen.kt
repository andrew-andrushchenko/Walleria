package com.andrii_a.walleria.ui.photo_details

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.*
import com.andrii_a.walleria.ui.login.LoginActivity
import com.andrii_a.walleria.ui.photo_details.components.*
import com.andrii_a.walleria.ui.theme.PrimaryDark
import com.andrii_a.walleria.ui.theme.PrimaryLight
import com.andrii_a.walleria.ui.util.*
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun PhotoDetailsScreen(
    photoId: PhotoId,
    loadResult: PhotoLoadResult,
    isUserLoggedIn: Boolean,
    isPhotoLiked: Boolean,
    isPhotoBookmarked: Boolean,
    dispatchPhotoDetailsEvent: (PhotoDetailsEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
    navigateToBookmarkPhoto: (PhotoId, PhotoUrl) -> Unit,
    navigateToSearch: (SearchQuery) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        when (loadResult) {
            is PhotoLoadResult.Empty -> Unit
            is PhotoLoadResult.Loading -> {
                LoadingSection(
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }
            is PhotoLoadResult.Error -> {
                ErrorSection(
                    onRetry = {
                        dispatchPhotoDetailsEvent(PhotoDetailsEvent.PhotoRequested(photoId.value))
                    },
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }
            is PhotoLoadResult.Success -> {
                ContentSection(
                    photo = loadResult.photo,
                    isUserLoggedIn = isUserLoggedIn,
                    isPhotoLiked = isPhotoLiked,
                    isPhotoBookmarked = isPhotoBookmarked,
                    navigateBack = navigateBack,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateToCollectPhoto = navigateToBookmarkPhoto,
                    navigateToSearch = navigateToSearch,
                    dispatchPhotoDetailsEvent = dispatchPhotoDetailsEvent,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@JvmInline
value class LikeCount(val value: Long)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ContentSection(
    photo: Photo,
    isUserLoggedIn: Boolean,
    isPhotoLiked: Boolean,
    isPhotoBookmarked: Boolean,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
    navigateToCollectPhoto: (PhotoId, PhotoUrl) -> Unit,
    navigateToSearch: (SearchQuery) -> Unit,
    dispatchPhotoDetailsEvent: (PhotoDetailsEvent) -> Unit
) {
    val context = LocalContext.current
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetContent = {
            PhotoInfoBottomSheet(
                photo = photo,
                navigateToUserDetails = navigateToUserDetails,
                navigateToSearch = navigateToSearch
            )
        },
        sheetState = modalBottomSheetState
    ) {
        BoxWithConstraints(modifier = modifier) {
            val constraints = this

            var areControlsVisible by rememberSaveable { mutableStateOf(true) }
            var zoomToFillCoefficient by rememberSaveable { mutableStateOf(1f) }

            val state = rememberZoomableState(
                minScale = 0.5f,
                maxScale = 6f,
                overZoomConfig = OverZoomConfig(1f, 4f)
            )

            Zoomable(
                state = state,
                enabled = true,
                onTap = { areControlsVisible = !areControlsVisible },
                dismissGestureEnabled = true,
                onDismiss = {
                    navigateBack()
                    true
                },
                modifier = Modifier.graphicsLayer {
                    clip = true
                    alpha = 1 - state.dismissDragProgress
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

            val scope = rememberCoroutineScope()

            AnimatedVisibility(
                visible = areControlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        alpha = 1 - state.dismissDragProgress
                    }
            ) {
                TopSection(
                    onNavigateBack = navigateBack,
                    onOpenInBrowser = { context.openPhotoInBrowser(photo.links?.html) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.6f),
                                    Color.Transparent
                                )
                            )
                        )
                        .statusBarsPadding()
                        .padding(8.dp)
                )
            }

            AnimatedVisibility(
                visible = areControlsVisible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .graphicsLayer {
                        alpha = 1 - state.dismissDragProgress
                    }
            ) {
                BottomSection(
                    likes = photo.likes ?: 0,
                    photoOwner = photo.user,
                    isPhotoLiked = isPhotoLiked,
                    isPhotoBookmarked = isPhotoBookmarked,
                    onNavigateToUserDetails = {
                        navigateToUserDetails(UserNickname(photo.userNickname))
                    },
                    onNavigateToCollectPhoto = {
                        navigateToCollectPhoto(
                            PhotoId(photo.id),
                            PhotoUrl(photo.getUrlByQuality(quality = PhotoQuality.LOW))
                        )
                    },
                    onLikeButtonClick = {
                        if (isUserLoggedIn) {
                            if (isPhotoLiked) {
                                dispatchPhotoDetailsEvent(PhotoDetailsEvent.PhotoDisliked(photo.id))
                                LikeCount(value = photo.likes ?: 1)
                            } else {
                                dispatchPhotoDetailsEvent(PhotoDetailsEvent.PhotoLiked(photo.id))
                                LikeCount(value = photo.likes?.plus(1) ?: 0)
                            }
                        } else {
                            context.toast(stringRes = R.string.login_to_like_photo)
                            context.startActivity(LoginActivity::class.java)
                            null
                        }
                    },
                    onInfoButtonClick = {
                        scope.launch {
                            modalBottomSheetState.show()
                        }
                    },
                    onShareButtonClick = {
                        context.sharePhoto(
                            photo.links?.html,
                            photo.description.orEmpty()
                        )
                    },
                    onDownloadButtonClick = { /*TODO*/ },
                    onZoomToFillClick = {
                        scope.launch {
                            state.animateScaleTo(
                                if (state.scale >= zoomToFillCoefficient) 1f
                                else zoomToFillCoefficient
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.8f)
                                )
                            )
                        )
                        .navigationBarsPadding()
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingSection(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LoadingBanner(
            modifier = Modifier
                .fillMaxSize()
                .background(PrimaryDark.copy(alpha = 0.4f))
                .statusBarsPadding()
        )

        TopSection(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(8.dp)
        )
    }
}

@Composable
fun ErrorSection(
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        ErrorBanner(
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize()
        )

        TopSection(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(8.dp)
        )
    }
}

@Composable
fun TopSection(
    onNavigateBack: () -> Unit,
    onOpenInBrowser: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = PrimaryLight
            )
        }

        IconButton(onClick = onOpenInBrowser) {
            Icon(
                painter = painterResource(id = R.drawable.ic_web_outlined),
                contentDescription = null,
                tint = PrimaryLight
            )
        }
    }
}

@Composable
fun TopSection(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = PrimaryLight
            )
        }
    }
}

@Composable
fun BottomSection(
    likes: Long,
    photoOwner: User?,
    isPhotoLiked: Boolean,
    isPhotoBookmarked: Boolean,
    onNavigateToUserDetails: () -> Unit,
    onNavigateToCollectPhoto: () -> Unit,
    onLikeButtonClick: () -> LikeCount?,
    onInfoButtonClick: () -> Unit,
    onShareButtonClick: () -> Unit,
    onDownloadButtonClick: () -> Unit,
    onZoomToFillClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            LikeAndBookmarkRow(
                likes = likes,
                isPhotoLiked = isPhotoLiked,
                isPhotoBookmarked = isPhotoBookmarked,
                onLikeButtonClick = onLikeButtonClick,
                onNavigateToCollectPhoto = onNavigateToCollectPhoto
            )

            IconButton(onClick = onZoomToFillClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_zoom_to_fill_outlined),
                    contentDescription = null,
                    tint = PrimaryLight
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            UserRow(
                userProfileImageUrl = photoOwner?.getProfileImageUrlOrEmpty().orEmpty(),
                username = photoOwner?.userFullName.orEmpty(),
                onUserClick = onNavigateToUserDetails,
            )

            InfoShareAndDownloadRow(
                onShareClick = onShareButtonClick,
                onInfoButtonClick = onInfoButtonClick,
                onDownloadButtonClick = onDownloadButtonClick
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

