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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.collections.CollectionsList
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.ErrorBanner
import com.andrii_a.walleria.ui.common.LoadingBanner
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.photos.PhotosList
import com.andrii_a.walleria.ui.theme.PrimaryDark
import com.andrii_a.walleria.ui.user_details.components.NestedScrollLayout
import com.andrii_a.walleria.ui.user_details.components.UserDetailsTopBar
import com.andrii_a.walleria.ui.user_details.components.UserHeader
import com.andrii_a.walleria.ui.user_details.components.rememberNestedScrollLayoutState
import kotlinx.coroutines.launch

@Composable
fun UserDetailsScreen(
    loadResult: UserLoadResult,
    onRetryLoading: (String) -> Unit,
    navigateBack: () -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateToEditUserProfile: () -> Unit,
    navigateToSearch: (SearchQuery) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (loadResult) {
            is UserLoadResult.Empty -> Unit
            is UserLoadResult.Loading -> {
                LoadingStateContent(
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            is UserLoadResult.Error -> {
                ErrorStateContent(
                    onRetry = { onRetryLoading(loadResult.userNickname) },
                    onNavigateBack = navigateBack,
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                )
            }

            is UserLoadResult.Success -> {
                SuccessStateContent(
                    user = loadResult.user,
                    loggedInUserNickname = loadResult.loggedInUserNickname,
                    userPhotosLazyItems = loadResult.userPhotos.collectAsLazyPagingItems(),
                    userLikedPhotosLazyItems = loadResult.userLikedPhotos.collectAsLazyPagingItems(),
                    userCollectionsLazyItems = loadResult.userCollections.collectAsLazyPagingItems(),
                    navigateBack = navigateBack,
                    navigateToPhotoDetails = navigateToPhotoDetails,
                    navigateToCollectionDetails = navigateToCollectionDetails,
                    navigateToSearch = navigateToSearch,
                    navigateToEditUserProfile = navigateToEditUserProfile
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
        )

        UserDetailsTopBar(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
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

        UserDetailsTopBar(
            onNavigateBack = onNavigateBack,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(8.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun SuccessStateContent(
    user: User,
    loggedInUserNickname: String,
    userPhotosLazyItems: LazyPagingItems<Photo>,
    userLikedPhotosLazyItems: LazyPagingItems<Photo>,
    userCollectionsLazyItems: LazyPagingItems<Collection>,
    navigateBack: () -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateToEditUserProfile: () -> Unit,
    navigateToSearch: (SearchQuery) -> Unit,
    modifier: Modifier = Modifier
) {
    val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetContent = {
            UserInfoBottomSheet(
                user = user,
                navigateToSearch = navigateToSearch
            )
        },
        sheetState = modalBottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Box(modifier = modifier) {
            val scope = rememberCoroutineScope()
            val pagerState = rememberPagerState(initialPage = 0) { UserDetailsScreenTabs.values().size }

            val nestedScrollLayoutState = rememberNestedScrollLayoutState()

            NestedScrollLayout(
                state = nestedScrollLayoutState,
                collapsableHeader = {
                    UserHeader(user = user)
                },
                modifier = Modifier.padding(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 64.dp
                )
            ) {
                Column {
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        backgroundColor = MaterialTheme.colors.surface,
                        contentColor = MaterialTheme.colors.onSurface,
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                    .height(4.dp)
                                    .padding(horizontal = 28.dp)
                                    .background(
                                        color = MaterialTheme.colors.onSurface,
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
                        navigateToPhotoDetails = navigateToPhotoDetails,
                        navigateToCollectionDetails = navigateToCollectionDetails
                    )
                }
            }

            UserDetailsTopBar(
                onNavigateBack = navigateBack,
                titleText = user.username,
                isOwnProfile = user.username == loggedInUserNickname,
                onEditProfile = navigateToEditUserProfile,
                onOpenMoreAboutProfile = { scope.launch { modalBottomSheetState.show() } }
            )
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
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = contentPadding
    ) { index ->
        when (index) {
            UserDetailsScreenTabs.Photos.ordinal -> {
                val listState = rememberLazyListState()

                ScrollToTopLayout(
                    listState = listState,
                    contentPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 8.dp
                    )
                ) {
                    PhotosList(
                        lazyPhotoItems = lazyPhotoItems,
                        onPhotoClicked = navigateToPhotoDetails,
                        onUserProfileClicked = {},
                        listState = listState,
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 64.dp
                        )
                    )
                }
            }

            UserDetailsScreenTabs.LikedPhotos.ordinal -> {
                val listState = rememberLazyListState()

                ScrollToTopLayout(
                    listState = listState,
                    contentPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 8.dp
                    )
                ) {
                    PhotosList(
                        lazyPhotoItems = lazyLikedPhotoItems,
                        onPhotoClicked = navigateToPhotoDetails,
                        onUserProfileClicked = {},
                        listState = listState,
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 64.dp
                        )
                    )
                }
            }

            UserDetailsScreenTabs.Collections.ordinal -> {
                val listState = rememberLazyListState()

                ScrollToTopLayout(
                    listState = listState,
                    contentPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 8.dp
                    )
                ) {
                    CollectionsList(
                        lazyCollectionItems = lazyCollectionItems,
                        onCollectionClicked = navigateToCollectionDetails,
                        onUserProfileClicked = {},
                        onPhotoClicked = navigateToPhotoDetails,
                        listState = listState,
                        contentPadding = PaddingValues(
                            top = 8.dp,
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 64.dp
                        )
                    )
                }
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
