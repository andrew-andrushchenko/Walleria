package com.andrii_a.walleria.ui.user_details

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
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
import com.andrii_a.walleria.ui.common.components.NestedScrollLayout
import com.andrii_a.walleria.ui.common.components.PhotosGridContent
import com.andrii_a.walleria.ui.common.components.WLoadingIndicator
import com.andrii_a.walleria.ui.common.components.rememberNestedScrollLayoutState
import com.andrii_a.walleria.ui.theme.WalleriaTheme
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
        WLoadingIndicator(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SuccessStateContent(
    state: UserDetailsUiState,
    onEvent: (UserDetailsEvent) -> Unit
) {
    val user = state.user!!

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val toolbarScrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        exitDirection = FloatingToolbarExitDirection.Bottom
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
                    FilledTonalIconButton(
                        onClick = { onEvent(UserDetailsEvent.GoBack) },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
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
                                imageVector = Icons.Filled.Edit,
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
                                contentDescription = stringResource(id = R.string.more_about_profile)
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
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.nestedScroll(toolbarScrollBehavior)
    ) { innerPadding ->
        val pagerState = rememberPagerState(initialPage = 0) { UserDetailsScreenTabs.entries.size }

        val nestedScrollLayoutState = rememberNestedScrollLayoutState()

        Box(modifier = Modifier.fillMaxSize()) {
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
                Pages(
                    pagerState = pagerState,
                    uiState = state,
                    onEvent = onEvent
                )
            }

            ProfileContentToolbar(
                pagerState = pagerState,
                scrollBehavior = toolbarScrollBehavior,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(
                        y = -ScreenOffset - WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
            )
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
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ProfileContentToolbar(
    pagerState: PagerState,
    scrollBehavior: FloatingToolbarScrollBehavior,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    HorizontalFloatingToolbar(
        expanded = true,
        scrollBehavior = scrollBehavior,
        modifier = modifier,
    ) {
        val options = UserDetailsScreenTabs.entries

        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            options.forEachIndexed { index, label ->
                ToggleButton(
                    checked = index == pagerState.currentPage,
                    onCheckedChange = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    modifier = Modifier.semantics { role = Role.RadioButton },
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        }
                ) {
                    Text(
                        text = stringResource(label.titleRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
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
                    contentPadding = contentPadding,
                    scrollToTopButtonEnabled = false,
                )
            }

            UserDetailsScreenTabs.LikedPhotos.ordinal -> {
                val lazyLikedPhotoItems by rememberUpdatedState(newValue = uiState.likedPhotos.collectAsLazyPagingItems())

                PhotosGridContent(
                    photoItems = lazyLikedPhotoItems,
                    onPhotoClicked = { onEvent(UserDetailsEvent.SelectPhoto(it)) },
                    contentPadding = contentPadding,
                    scrollToTopButtonEnabled = false,
                )
            }

            UserDetailsScreenTabs.Collections.ordinal -> {
                val lazyCollectionItems by rememberUpdatedState(newValue = uiState.collections.collectAsLazyPagingItems())

                CollectionsGridContent(
                    collectionItems = lazyCollectionItems,
                    onCollectionClick = { onEvent(UserDetailsEvent.SelectCollection(it)) },
                    contentPadding = contentPadding,
                    scrollToTopButtonEnabled = false,
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
            user = user,
        )

        UserDetailsScreen(
            state = state,
            onEvent = {}
        )
    }
}
