package com.andrii_a.walleria.ui.user_details

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.domain.models.user.UserSocialMediaLinks
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.components.CollectionsGridContent
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.LoadingListItem
import com.andrii_a.walleria.ui.common.components.PhotosGridContent
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.user_details.components.NestedScrollLayout
import com.andrii_a.walleria.ui.user_details.components.UserHeader
import com.andrii_a.walleria.ui.user_details.components.rememberNestedScrollLayoutState
import kotlinx.coroutines.launch

@Composable
fun UserDetailsScreen(
    state: UserDetailsUiState,
    onEvent: (UserDetailsEvent) -> Unit
) {
    when {
        state.isLoading -> {
            LoadingStateContent(
                onNavigateBack = { onEvent(UserDetailsEvent.GoBack) }
            )
        }

        !state.isLoading && state.error == null && state.user != null -> {
            SuccessStateContent(
                state = state,
                onEvent = onEvent
            )
        }

        else -> {
            ErrorStateContent(
                onRetry = {
                    val error = state.error as? UiErrorWithRetry
                    error?.onRetry?.invoke()
                },
                onNavigateBack = { onEvent(UserDetailsEvent.GoBack) }
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
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LoadingListItem()
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
fun SuccessStateContent(
    state: UserDetailsUiState,
    onEvent: (UserDetailsEvent) -> Unit
) {
    val user = state.user!!

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = user.username,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(UserDetailsEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                },
                actions = {
                    if (state.loggedInUserNickname == user.username) {
                        IconButton(onClick = { onEvent(UserDetailsEvent.SelectEditProfile) }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = stringResource(id = R.string.edit_collection)
                            )
                        }
                    }

                    Box(modifier = Modifier) {
                        var menuExpanded by rememberSaveable {
                            mutableStateOf(false)
                        }

                        IconButton(
                            onClick = { menuExpanded = !menuExpanded }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(id = R.string.edit_collection)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.open_in_browser))
                                },
                                onClick = {
                                    onEvent(UserDetailsEvent.OpenUserProfileInBrowser(user.username))
                                    menuExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = R.string.more_about_profile))
                                },
                                onClick = {
                                    onEvent(UserDetailsEvent.OpenDetailsDialog)
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        val pagerState = rememberPagerState(initialPage = 0) { UserDetailsScreenTabs.entries.size }

        val nestedScrollLayoutState = rememberNestedScrollLayoutState()

        NestedScrollLayout(
            state = nestedScrollLayoutState,
            collapsableHeader = {
                UserHeader(
                    user = user,
                    onOpenPortfolio = { onEvent(UserDetailsEvent.SelectPortfolioLink(it)) },
                    onOpenInstagramProfile = { onEvent(UserDetailsEvent.SelectInstagramProfile(it)) },
                    onOpenTwitterProfile = { onEvent(UserDetailsEvent.SelectTwitterProfile(it)) },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            Column {
                Tabs(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(450.dp)
                        .widthIn(min = 200.dp, max = 600.dp)
                        .padding(horizontal = 16.dp)
                )

                Pages(
                    pagerState = pagerState,
                    uiState = state,
                    onEvent = onEvent
                )
            }
        }

        if (state.isDetailsDialogOpened) {
            ModalBottomSheet(
                onDismissRequest = { onEvent(UserDetailsEvent.DismissDetailsDialog) },
                sheetState = bottomSheetState
            ) {
                UserInfoBottomSheet(
                    user = user,
                    navigateToSearch = { query ->
                        onEvent(UserDetailsEvent.SearchByTag(query))
                    }
                )
            }
        }
    }
}

@Composable
private fun Tabs(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        val options = UserDetailsScreenTabs.entries

        options.forEachIndexed { index, tabPage ->
            SegmentedButton(
                selected = index == pagerState.currentPage,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                label = {
                    Text(
                        text = stringResource(id = tabPage.titleRes),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            )
        }
    }
}

@Composable
private fun Pages(
    pagerState: PagerState,
    uiState: UserDetailsUiState,
    onEvent: (UserDetailsEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = contentPadding
    ) { index ->
        when (index) {
            UserDetailsScreenTabs.Photos.ordinal -> {
                val lazyPhotoItems by rememberUpdatedState(newValue = uiState.photos.collectAsLazyPagingItems())

                PhotosGridContent(
                    photoItems = lazyPhotoItems,
                    onPhotoClicked = { onEvent(UserDetailsEvent.SelectPhoto(it)) },
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    ),
                )
            }

            UserDetailsScreenTabs.LikedPhotos.ordinal -> {
                val lazyLikedPhotoItems by rememberUpdatedState(newValue = uiState.likedPhotos.collectAsLazyPagingItems())

                PhotosGridContent(
                    photoItems = lazyLikedPhotoItems,
                    onPhotoClicked = { onEvent(UserDetailsEvent.SelectPhoto(it)) },
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    ),
                )
            }

            UserDetailsScreenTabs.Collections.ordinal -> {
                val lazyCollectionItems by rememberUpdatedState(newValue = uiState.collections.collectAsLazyPagingItems())

                CollectionsGridContent(
                    collectionItems = lazyCollectionItems,
                    onCollectionClick = { onEvent(UserDetailsEvent.SelectCollection(it)) },
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    ),
                )
            }

            else -> throw IllegalStateException("Tab screen was not declared!")
        }
    }
}

private enum class UserDetailsScreenTabs(@StringRes val titleRes: Int) {
    Photos(R.string.photos),
    LikedPhotos(R.string.liked_photos),
    Collections(R.string.collections)
}

@PreviewScreenSizes
@Composable
fun UserDetailsScreenPreview() {
    WalleriaTheme {
        Surface {
            val user = User(
                id = "",
                username = "john_smith",
                firstName = "John",
                lastName = "Smith",
                bio = "",
                location = "San Francisco, California, USA",
                totalLikes = 100,
                totalPhotos = 100,
                totalCollections = 100,
                followersCount = 100_000,
                followingCount = 56,
                downloads = 99_000,
                profileImage = null,
                social = UserSocialMediaLinks(
                    instagramUsername = "a",
                    portfolioUrl = "a",
                    twitterUsername = "a",
                    paypalEmail = "a"
                ),
                tags = null,
                photos = null
            )

            val state = UserDetailsUiState(
                isLoading = false,
                error = null,
                loggedInUserNickname = "john_smith",
                user = user
            )

            UserDetailsScreen(
                state = state,
                onEvent = {}
            )
        }
    }
}
