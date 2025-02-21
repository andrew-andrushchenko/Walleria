package com.andrii_a.walleria.ui.user_details

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.UiErrorWithRetry
import com.andrii_a.walleria.ui.common.components.CollectionsGridContent
import com.andrii_a.walleria.ui.common.components.ErrorBanner
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
            TopBar(
                onNavigateBack = { onEvent(UserDetailsEvent.GoBack) },
                titleText = user.username,
                isOwnProfile = user.username == state.loggedInUserNickname,
                onEditProfile = { onEvent(UserDetailsEvent.SelectEditProfile) },
                onOpenMoreAboutProfile = { onEvent(UserDetailsEvent.OpenDetailsDialog) },
                onOpenProfileInBrowser = { onEvent(UserDetailsEvent.OpenUserProfileInBrowser(user.username)) }
            )
        }
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
                    modifier = Modifier.fillMaxWidth()
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
    val scope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    .height(4.dp)
                    .padding(horizontal = 32.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        },
        modifier = modifier
    ) {
        UserDetailsScreenTabs.entries.forEachIndexed { index, tabPage ->
            Tab(
                selected = index == pagerState.currentPage,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                text = {
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
                        bottom = WindowInsets.systemBars.asPaddingValues()
                            .calculateBottomPadding() + 150.dp,
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 90.dp
                    )
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
                        bottom = WindowInsets.systemBars.asPaddingValues()
                            .calculateBottomPadding() + 150.dp,
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 90.dp
                    )
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
                        bottom = WindowInsets.systemBars.asPaddingValues()
                            .calculateBottomPadding() + 150.dp,
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 90.dp
                    )
                )
            }

            else -> throw IllegalStateException("Tab screen was not declared!")
        }
    }
}

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    isOwnProfile: Boolean = false,
    onNavigateBack: () -> Unit,
    onEditProfile: () -> Unit,
    onOpenMoreAboutProfile: () -> Unit,
    onOpenProfileInBrowser: () -> Unit
) {
    Surface(
        modifier = modifier.height(
            dimensionResource(id = R.dimen.top_bar_height) +
                    WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateTopPadding()
        )
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (backButton, title, editButton, dropdownMenuBox) = createRefs()

            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .constrainAs(backButton) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start, 8.dp)
                        if (!titleText.isNullOrBlank()) {
                            end.linkTo(title.start)
                        }
                    }
                    .statusBarsPadding()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_back),
                )
            }

            titleText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .constrainAs(title) {
                            top.linkTo(backButton.top)
                            bottom.linkTo(backButton.bottom)
                            start.linkTo(backButton.end, 16.dp)
                            if (isOwnProfile) {
                                end.linkTo(editButton.start)
                            } else {
                                end.linkTo(dropdownMenuBox.end)
                            }
                            width = Dimension.fillToConstraints
                        }
                        .statusBarsPadding()
                )
            }

            if (isOwnProfile) {
                IconButton(
                    onClick = onEditProfile,
                    modifier = Modifier
                        .constrainAs(editButton) {
                            top.linkTo(backButton.top)
                            bottom.linkTo(backButton.bottom)
                            end.linkTo(dropdownMenuBox.start)
                        }
                        .statusBarsPadding()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = stringResource(id = R.string.edit_collection)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .constrainAs(dropdownMenuBox) {
                        top.linkTo(backButton.top)
                        bottom.linkTo(backButton.bottom)
                        end.linkTo(parent.end, 8.dp)
                    }
                    .statusBarsPadding()
            ) {
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
                        onClick = onOpenProfileInBrowser
                    )

                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(id = R.string.more_about_profile))
                        },
                        onClick = onOpenMoreAboutProfile
                    )
                }
            }
        }
    }
}

private enum class UserDetailsScreenTabs(@StringRes val titleRes: Int) {
    Photos(R.string.photos),
    LikedPhotos(R.string.liked_photos),
    Collections(R.string.collections)
}

@Preview
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
                social = null,
                tags = null,
                photos = null
            )

            val state = UserDetailsUiState(
                isLoading = false,
                error = null,
                user = user
            )

            UserDetailsScreen(state = state, onEvent = {})
        }
    }
}
