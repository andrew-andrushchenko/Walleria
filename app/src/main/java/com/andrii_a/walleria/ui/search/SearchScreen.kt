package com.andrii_a.walleria.ui.search

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.search.RecentSearchItem
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.lists.CollectionsGrid
import com.andrii_a.walleria.ui.common.components.lists.CollectionsList
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
import com.andrii_a.walleria.ui.common.components.lists.UsersList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    query: String,
    recentSearches: List<RecentSearchItem>,
    photos: Flow<PagingData<Photo>>,
    collections: Flow<PagingData<Collection>>,
    users: Flow<PagingData<User>>,
    photoFilters: StateFlow<PhotoFilters>,
    photosListLayoutType: PhotosListLayoutType,
    collectionListLayoutType: CollectionListLayoutType,
    photosLoadQuality: PhotoQuality,
    onEvent: (SearchScreenEvent) -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit,
    navigateBack: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0) { SearchScreenTabs.entries.size }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val filters by photoFilters.collectAsStateWithLifecycle()

    var text by rememberSaveable { mutableStateOf(query) }
    var active by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SearchBar(
                query = text,
                onQueryChange = { text = it },
                onSearch = {
                    active = false
                    onEvent(SearchScreenEvent.SaveRecentSearch(query = text))
                    onEvent(SearchScreenEvent.ChangeQuery(query = text))
                },
                active = active,
                onActiveChange = { active = it },
                placeholder = { Text(stringResource(id = R.string.type_something)) },
                leadingIcon = {
                    AnimatedContent(
                        targetState = active,
                        label = ""
                    ) { state ->
                        if (state) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        } else {
                            IconButton(onClick = navigateBack) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = stringResource(id = R.string.navigate_back)
                                )
                            }
                        }
                    }
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = (pagerState.currentPage == SearchScreenTabs.Photos.ordinal) && !active,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        IconButton(onClick = { openBottomSheet = !openBottomSheet }) {
                            Icon(
                                imageVector = Icons.Outlined.FilterList,
                                contentDescription = stringResource(id = R.string.photo_filters)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .semantics { traversalIndex = -1f },
            ) {
                Text(
                    text = stringResource(id = R.string.recent_searches),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecentSearchesList(
                    recentSearches = recentSearches,
                    onItemSelected = { item ->
                        active = false
                        text = item.title
                        onEvent(SearchScreenEvent.SaveRecentSearch(query = text))
                        onEvent(SearchScreenEvent.ChangeQuery(query = text))
                    },
                    onDeleteItem = { item ->
                        onEvent(SearchScreenEvent.DeleteRecentSearch(item))
                    },
                    onDeleteAllItems = {
                        onEvent(SearchScreenEvent.DeleteAllRecentSearches)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SearchTabs(
                pagerState = pagerState,
                modifier = Modifier.fillMaxWidth()
            )

            Pages(
                pagerState = pagerState,
                photos = photos,
                collections = collections,
                users = users,
                photosListLayoutType = photosListLayoutType,
                collectionListLayoutType = collectionListLayoutType,
                photosLoadQuality = photosLoadQuality,
                navigateToPhotoDetails = navigateToPhotoDetails,
                navigateToCollectionDetails = navigateToCollectionDetails,
                navigateToUserDetails = navigateToUserDetails
            )
        }
    }

    val scope = rememberCoroutineScope()

    if (openBottomSheet) {
        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0),
        ) {
            SearchPhotoFiltersBottomSheet(
                photoFilters = filters,
                contentPadding = PaddingValues(
                    bottom = bottomPadding
                ),
                onApplyClick = onEvent,
                onDismiss = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            openBottomSheet = false
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchTabs(
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
        SearchScreenTabs.entries.forEachIndexed { index, tabPage ->
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Pages(
    pagerState: PagerState,
    photos: Flow<PagingData<Photo>>,
    collections: Flow<PagingData<Collection>>,
    users: Flow<PagingData<User>>,
    photosListLayoutType: PhotosListLayoutType,
    collectionListLayoutType: CollectionListLayoutType,
    photosLoadQuality: PhotoQuality,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = contentPadding
    ) { index ->
        when (index) {
            SearchScreenTabs.Photos.ordinal -> {
                val lazyPhotoItems = photos.collectAsLazyPagingItems()

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
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                            ),
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
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                            ),
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
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            SearchScreenTabs.Collections.ordinal -> {
                val lazyCollectionItems = collections.collectAsLazyPagingItems()

                when (collectionListLayoutType) {
                    CollectionListLayoutType.DEFAULT -> {
                        val listState = rememberLazyListState()

                        CollectionsList(
                            lazyCollectionItems = lazyCollectionItems,
                            onCollectionClicked = navigateToCollectionDetails,
                            onUserProfileClicked = navigateToUserDetails,
                            onPhotoClicked = navigateToPhotoDetails,
                            photosLoadQuality = photosLoadQuality,
                            listState = listState,
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                            )
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
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                            )
                        )
                    }

                    CollectionListLayoutType.GRID -> {
                        val gridState = rememberLazyGridState()

                        CollectionsGrid(
                            lazyCollectionItems = lazyCollectionItems,
                            onCollectionClicked = navigateToCollectionDetails,
                            gridState = gridState,
                            photosLoadQuality = photosLoadQuality,
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                            )
                        )
                    }
                }
            }

            SearchScreenTabs.Users.ordinal -> {
                val lazyUserItems = users.collectAsLazyPagingItems()

                val listState = rememberLazyListState()

                UsersList(
                    lazyUserItems = lazyUserItems,
                    onUserClick = navigateToUserDetails,
                    listState = listState,
                    contentPadding = PaddingValues(
                        top = dimensionResource(id = R.dimen.list_top_padding),
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                    )
                )
            }

            else -> throw IllegalStateException("Tab screen was not declared!")
        }
    }
}

private enum class SearchScreenTabs(@StringRes val titleRes: Int) {
    Photos(R.string.photos),
    Collections(R.string.collections),
    Users(R.string.users)
}