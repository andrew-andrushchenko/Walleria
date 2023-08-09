package com.andrii_a.walleria.ui.photo_details

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.content.ContextCompat
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.PhotoQuality
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.models.user.UserProfileImage
import com.andrii_a.walleria.ui.common.*
import com.andrii_a.walleria.ui.login.LoginActivity
import com.andrii_a.walleria.ui.photo_details.components.*
import com.andrii_a.walleria.ui.theme.OnPrimaryLight
import com.andrii_a.walleria.ui.theme.PrimaryDark
import com.andrii_a.walleria.ui.theme.PrimaryLight
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.*
import kotlinx.coroutines.launch
import kotlin.math.max

@Composable
fun PhotoDetailsScreen(
    photoId: PhotoId,
    loadResult: PhotoLoadResult,
    isUserLoggedIn: Boolean,
    isPhotoLiked: Boolean,
    isPhotoCollected: Boolean,
    onEvent: (PhotoDetailsEvent) -> Unit,
    navigateBack: () -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
    navigateToCollectPhoto: (PhotoId) -> Unit,
    navigateToSearch: (SearchQuery) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {
        when (loadResult) {
            is PhotoLoadResult.Empty -> Unit
            is PhotoLoadResult.Loading -> {
                LoadingStateContent(
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            is PhotoLoadResult.Error -> {
                ErrorStateContent(
                    onRetry = {
                        onEvent(PhotoDetailsEvent.RequestPhoto(photoId.value))
                    },
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            is PhotoLoadResult.Success -> {
                SuccessStateContent(
                    photo = loadResult.photo,
                    isUserLoggedIn = isUserLoggedIn,
                    isPhotoLiked = isPhotoLiked,
                    isPhotoCollected = isPhotoCollected,
                    navigateBack = navigateBack,
                    navigateToUserDetails = navigateToUserDetails,
                    navigateToCollectPhoto = navigateToCollectPhoto,
                    navigateToSearch = navigateToSearch,
                    navigateToCollectionDetails = navigateToCollectionDetails,
                    onEvent = onEvent,
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
private fun SuccessStateContent(
    photo: Photo,
    isUserLoggedIn: Boolean,
    isPhotoLiked: Boolean,
    isPhotoCollected: Boolean,
    modifier: Modifier = Modifier,
    navigateBack: () -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
    navigateToCollectPhoto: (PhotoId) -> Unit,
    navigateToSearch: (SearchQuery) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    onEvent: (PhotoDetailsEvent) -> Unit
) {
    val context = LocalContext.current
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetContent = {
            PhotoInfoBottomSheet(
                photo = photo,
                navigateToSearch = navigateToSearch,
                navigateToCollectionDetails = navigateToCollectionDetails
            )
        },
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        BoxWithConstraints(modifier = modifier) {
            val constraints = this

            var areControlsVisible by rememberSaveable { mutableStateOf(true) }
            var zoomToFillCoefficient by rememberSaveable { mutableFloatStateOf(1f) }

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
                TopBar(
                    onNavigateBack = navigateBack,
                    onOpenInBrowser = { context.openPhotoInBrowser(photo.links?.html) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Black.copy(alpha = 0.5f))
                        .statusBarsPadding()
                        .padding(8.dp)
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
                        alpha = 1 - state.dismissDragProgress
                    }
            ) {
                BottomControls(
                    likes = photo.likes,
                    photoOwner = photo.user,
                    isPhotoLiked = isPhotoLiked,
                    isPhotoCollected = isPhotoCollected,
                    onNavigateToUserDetails = {
                        navigateToUserDetails(UserNickname(photo.userNickname))
                    },
                    onNavigateToCollectPhoto = {
                        if (isUserLoggedIn) {
                            navigateToCollectPhoto(PhotoId(photo.id))
                        } else {
                            context.toast(stringRes = R.string.login_to_collect_photo)
                            context.startActivity(LoginActivity::class.java)
                        }
                    },
                    onLikeButtonClick = {
                        if (isUserLoggedIn) {
                            if (isPhotoLiked) {
                                onEvent(PhotoDetailsEvent.DislikePhoto(photo.id))
                                LikeCount(value = photo.likes)
                            } else {
                                onEvent(PhotoDetailsEvent.LikePhoto(photo.id))
                                LikeCount(value = photo.likes + 1)
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
                            onEvent(PhotoDetailsEvent.DownloadPhoto(photo))
                        }
                    },
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
                        .background(color = Color.Black.copy(alpha = 0.5f))
                        .navigationBarsPadding()
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingStateContent(
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

        TopBar(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(8.dp)
        )
    }
}

@Composable
private fun ErrorStateContent(
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        ErrorBanner(
            onRetry = onRetry,
            modifier = Modifier.fillMaxSize()
        )

        TopBar(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(8.dp)
        )
    }
}

@Composable
private fun TopBar(
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
private fun TopBar(
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
private fun BottomControls(
    likes: Long,
    photoOwner: User?,
    isPhotoLiked: Boolean,
    isPhotoCollected: Boolean,
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
                    painter = painterResource(
                        id = if (isPhotoLiked) R.drawable.ic_like_filled
                        else R.drawable.ic_like_outlined
                    ),
                    contentDescription = null
                )
            },
            onClick = {
                val likeCount = onLikeButtonClick()
                likeCount?.let { photoLikes = it.value }
            },
            backgroundColor = PrimaryLight,
            contentColor = OnPrimaryLight,
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
                    painter = painterResource(
                        id = if (isPhotoCollected) R.drawable.ic_bookmark_remove_filled
                        else R.drawable.ic_bookmark_add_outlined
                    ),
                    contentDescription = null
                )
            },
            onClick = onNavigateToCollectPhoto,
            backgroundColor = PrimaryLight,
            contentColor = OnPrimaryLight,
            modifier = Modifier.constrainAs(collectButton) {
                top.linkTo(likeButton.top)
                start.linkTo(likeButton.end, 4.dp)
                bottom.linkTo(likeButton.bottom)
            }
        )

        IconButton(
            onClick = onZoomToFillClick,
            modifier = Modifier.constrainAs(zoomToFillButton) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_zoom_to_fill_outlined),
                contentDescription = null,
                tint = PrimaryLight
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
                painter = painterResource(id = R.drawable.ic_about_outlined),
                contentDescription = null,
                tint = PrimaryLight
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
                painter = painterResource(id = R.drawable.ic_share_outlined),
                contentDescription = null,
                tint = PrimaryLight
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
                painter = painterResource(id = R.drawable.ic_download_outlined),
                contentDescription = null,
                tint = PrimaryLight
            )
        }
    }
}

@Preview
@Composable
fun BottomControlsPreview() {
    WalleriaTheme {
        BottomControls(
            likes = 10,
            photoOwner = User(
                id = "",
                username = "",
                firstName = "Very very very long name",
                lastName = "Smith",
                bio = null,
                location = null,
                totalLikes = 0,
                totalPhotos = 0,
                totalCollections = 0,
                followersCount = 0,
                followingCount = 0,
                downloads = 0,
                profileImage = UserProfileImage(
                    small = "",
                    medium = "",
                    large = ""
                ),
                social = null,
                tags = null,
                photos = null
            ),
            isPhotoLiked = true,
            isPhotoCollected = false,
            onNavigateToUserDetails = {},
            onNavigateToCollectPhoto = {},
            onLikeButtonClick = { LikeCount(0) },
            onInfoButtonClick = {},
            onShareButtonClick = {},
            onDownloadButtonClick = {},
            onZoomToFillClick = {},
            modifier = Modifier.fillMaxWidth()
        )
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

