package com.andrii_a.walleria.ui.user_details

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.lists.CollectionsGrid
import com.andrii_a.walleria.ui.common.components.lists.CollectionsList
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
import com.andrii_a.walleria.ui.user_details.components.NestedScrollLayout
import com.andrii_a.walleria.ui.user_details.components.UserHeader
import com.andrii_a.walleria.ui.user_details.components.rememberNestedScrollLayoutState
import com.andrii_a.walleria.ui.util.openUserProfileInBrowser
import kotlinx.coroutines.launch

@Composable
fun UserDetailsScreen(
    loadResult: UserLoadResult,
    photosListLayoutType: PhotosListLayoutType,
    collectionsListLayoutType: CollectionListLayoutType,
    photosLoadQuality: PhotoQuality,
    onRetryLoading: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateToEditUserProfile: () -> Unit,
    navigateToSearch: (SearchQuery) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit
) {
    when (loadResult) {
        is UserLoadResult.Empty -> Unit
        is UserLoadResult.Loading -> {
            LoadingStateContent(
                onNavigateBack = navigateBack
            )
        }

        is UserLoadResult.Error -> {
            ErrorStateContent(
                onRetry = { onRetryLoading(loadResult.userNickname) },
                onNavigateBack = navigateBack
            )
        }

        is UserLoadResult.Success -> {
            SuccessStateContent(
                user = loadResult.user,
                loggedInUserNickname = loadResult.loggedInUserNickname,
                userPhotosLazyItems = loadResult.userPhotos.collectAsLazyPagingItems(),
                userLikedPhotosLazyItems = loadResult.userLikedPhotos.collectAsLazyPagingItems(),
                userCollectionsLazyItems = loadResult.userCollections.collectAsLazyPagingItems(),
                photosListLayoutType = photosListLayoutType,
                collectionsListLayoutType = collectionsListLayoutType,
                photosLoadQuality = photosLoadQuality,
                navigateBack = navigateBack,
                navigateToPhotoDetails = navigateToPhotoDetails,
                navigateToCollectionDetails = navigateToCollectionDetails,
                navigateToSearch = navigateToSearch,
                navigateToEditUserProfile = navigateToEditUserProfile,
                navigateToUserDetails = navigateToUserDetails
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
                            imageVector = Icons.Default.ArrowBack,
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
                            imageVector = Icons.Default.ArrowBack,
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


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SuccessStateContent(
    user: User,
    loggedInUserNickname: String,
    userPhotosLazyItems: LazyPagingItems<Photo>,
    userLikedPhotosLazyItems: LazyPagingItems<Photo>,
    userCollectionsLazyItems: LazyPagingItems<Collection>,
    photosListLayoutType: PhotosListLayoutType,
    collectionsListLayoutType: CollectionListLayoutType,
    photosLoadQuality: PhotoQuality,
    navigateBack: () -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateToEditUserProfile: () -> Unit,
    navigateToSearch: (SearchQuery) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateBack = navigateBack,
                titleText = user.username,
                isOwnProfile = user.username == loggedInUserNickname,
                onEditProfile = navigateToEditUserProfile,
                onOpenMoreAboutProfile = { openBottomSheet = !openBottomSheet }
            )
        }
    ) { innerPadding ->
        val scope = rememberCoroutineScope()
        val pagerState = rememberPagerState(initialPage = 0) { UserDetailsScreenTabs.values().size }

        val nestedScrollLayoutState = rememberNestedScrollLayoutState()

        NestedScrollLayout(
            state = nestedScrollLayoutState,
            collapsableHeader = {
                UserHeader(user = user)
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            Column {
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
                    }
                ) {
                    UserDetailsScreenTabs.values().forEachIndexed { index, tabPage ->
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

                Pages(
                    pagerState = pagerState,
                    lazyPhotoItems = userPhotosLazyItems,
                    lazyLikedPhotoItems = userLikedPhotosLazyItems,
                    lazyCollectionItems = userCollectionsLazyItems,
                    photosListLayoutType = photosListLayoutType,
                    collectionsListLayoutType = collectionsListLayoutType,
                    photosLoadQuality = photosLoadQuality,
                    navigateToPhotoDetails = navigateToPhotoDetails,
                    navigateToCollectionDetails = navigateToCollectionDetails,
                    navigateToUserDetails = navigateToUserDetails
                )
            }
        }

        if (openBottomSheet) {
            val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

            ModalBottomSheet(
                onDismissRequest = { openBottomSheet = false },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets(0)
            ) {
                UserInfoBottomSheet(
                    user = user,
                    contentPadding = PaddingValues(
                        bottom = bottomPadding
                    ),
                    navigateToSearch = navigateToSearch
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Pages(
    pagerState: PagerState,
    lazyPhotoItems: LazyPagingItems<Photo>,
    lazyLikedPhotoItems: LazyPagingItems<Photo>,
    lazyCollectionItems: LazyPagingItems<Collection>,
    photosListLayoutType: PhotosListLayoutType,
    collectionsListLayoutType: CollectionListLayoutType,
    photosLoadQuality: PhotoQuality,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = contentPadding
    ) { index ->
        when (index) {
            UserDetailsScreenTabs.Photos.ordinal -> {
                when (photosListLayoutType) {
                    PhotosListLayoutType.DEFAULT -> {
                        val listState = rememberLazyListState()

                        PhotosList(
                            lazyPhotoItems = lazyPhotoItems,
                            onPhotoClicked = navigateToPhotoDetails,
                            onUserProfileClicked = navigateToUserDetails,
                            isCompact = false,
                            photosLoadQuality = photosLoadQuality,
                            listState = listState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    PhotosListLayoutType.MINIMAL_LIST -> {
                        val listState = rememberLazyListState()

                        PhotosList(
                            lazyPhotoItems = lazyPhotoItems,
                            onPhotoClicked = navigateToPhotoDetails,
                            onUserProfileClicked = navigateToUserDetails,
                            isCompact = true,
                            photosLoadQuality = photosLoadQuality,
                            listState = listState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    PhotosListLayoutType.STAGGERED_GRID -> {
                        val gridState = rememberLazyStaggeredGridState()

                        PhotosGrid(
                            lazyPhotoItems = lazyPhotoItems,
                            onPhotoClicked = navigateToPhotoDetails,
                            photosLoadQuality = photosLoadQuality,
                            gridState = gridState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            UserDetailsScreenTabs.LikedPhotos.ordinal -> {
                when (photosListLayoutType) {
                    PhotosListLayoutType.DEFAULT -> {
                        val listState = rememberLazyListState()

                        PhotosList(
                            lazyPhotoItems = lazyLikedPhotoItems,
                            onPhotoClicked = navigateToPhotoDetails,
                            onUserProfileClicked = navigateToUserDetails,
                            isCompact = false,
                            photosLoadQuality = photosLoadQuality,
                            listState = listState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    PhotosListLayoutType.MINIMAL_LIST -> {
                        val listState = rememberLazyListState()

                        PhotosList(
                            lazyPhotoItems = lazyLikedPhotoItems,
                            onPhotoClicked = navigateToPhotoDetails,
                            onUserProfileClicked = navigateToUserDetails,
                            isCompact = true,
                            photosLoadQuality = photosLoadQuality,
                            listState = listState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    PhotosListLayoutType.STAGGERED_GRID -> {
                        val gridState = rememberLazyStaggeredGridState()

                        PhotosGrid(
                            lazyPhotoItems = lazyLikedPhotoItems,
                            onPhotoClicked = navigateToPhotoDetails,
                            photosLoadQuality = photosLoadQuality,
                            gridState = gridState,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            UserDetailsScreenTabs.Collections.ordinal -> {
                when (collectionsListLayoutType) {
                    CollectionListLayoutType.DEFAULT -> {
                        val listState = rememberLazyListState()

                        CollectionsList(
                            lazyCollectionItems = lazyCollectionItems,
                            onCollectionClicked = navigateToCollectionDetails,
                            onUserProfileClicked = navigateToUserDetails,
                            onPhotoClicked = navigateToPhotoDetails,
                            photosLoadQuality = photosLoadQuality,
                            listState = listState,
                        )
                    }

                    CollectionListLayoutType.MINIMAL_LIST -> {
                        val listState = rememberLazyListState()

                        CollectionsList(
                            lazyCollectionItems = lazyCollectionItems,
                            onCollectionClicked = navigateToCollectionDetails,
                            onUserProfileClicked = navigateToUserDetails,
                            onPhotoClicked = navigateToPhotoDetails,
                            photosLoadQuality = photosLoadQuality,
                            isCompact = true,
                            listState = listState,
                        )
                    }

                    CollectionListLayoutType.GRID -> {
                        val gridState = rememberLazyGridState()

                        CollectionsGrid(
                            lazyCollectionItems = lazyCollectionItems,
                            onCollectionClicked = navigateToCollectionDetails,
                            gridState = gridState,
                            photosLoadQuality = photosLoadQuality,
                        )
                    }
                }
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
    onOpenMoreAboutProfile: () -> Unit
) {
    val context = LocalContext.current

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
                    imageVector = Icons.Default.ArrowBack,
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
                        onClick = {
                            titleText?.let {
                                context.openUserProfileInBrowser(UserNickname(it))
                            }
                        }
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
