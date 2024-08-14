package com.andrii_a.walleria.ui.search

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.ui.common.components.lists.CollectionsGrid
import com.andrii_a.walleria.ui.common.components.lists.CollectionsList
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
import com.andrii_a.walleria.ui.common.components.lists.UsersList
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0) { SearchScreenTabs.entries.size }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var text by rememberSaveable { mutableStateOf(state.query) }
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
                    onEvent(SearchEvent.PerformSearch(query = text))
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
                            IconButton(onClick = { onEvent(SearchEvent.GoBack) }) {
                                Icon(
                                    Icons.AutoMirrored.Default.ArrowBack,
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
                        IconButton(onClick = { onEvent(SearchEvent.OpenFilterDialog) }) {
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
                    recentSearches = state.recentSearches,
                    onItemSelected = { item ->
                        active = false
                        text = item.title
                        onEvent(SearchEvent.PerformSearch(query = text))
                    },
                    onDeleteItem = { item ->
                        onEvent(SearchEvent.DeleteRecentSearchItem(item))
                    },
                    onDeleteAllItems = {
                        onEvent(SearchEvent.DeleteAllRecentSearches)
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
                uiState = state,
                onEvent = onEvent
            )
        }
    }

    val scope = rememberCoroutineScope()

    if (state.isFilterDialogOpened) {
        val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

        ModalBottomSheet(
            onDismissRequest = { onEvent(SearchEvent.DismissFilterDialog) },
            sheetState = bottomSheetState,
            windowInsets = WindowInsets(0),
        ) {
            SearchPhotoFiltersBottomSheet(
                photoFilters = state.photoFilters,
                contentPadding = PaddingValues(
                    bottom = bottomPadding
                ),
                onEvent = onEvent,
                onDismiss = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            onEvent(SearchEvent.DismissFilterDialog)
                        }
                    }
                }
            )
        }
    }
}

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

@Composable
private fun Pages(
    pagerState: PagerState,
    uiState: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = contentPadding
    ) { index ->
        when (index) {
            SearchScreenTabs.Photos.ordinal -> {
                val lazyPhotoItems = uiState.photos.collectAsLazyPagingItems()

                when (uiState.photosLayoutType) {
                    PhotosListLayoutType.DEFAULT -> {
                        val listState = rememberLazyListState()

                        PhotosList(
                            lazyPhotoItems = lazyPhotoItems,
                            onPhotoClicked = { id ->
                                onEvent(SearchEvent.SelectPhoto(id))
                            },
                            onUserProfileClicked = { nickname ->
                                onEvent(SearchEvent.SelectUser(nickname))
                            },
                            isCompact = false,
                            photosLoadQuality = uiState.photosLoadQuality,
                            listState = listState,
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding(),
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    PhotosListLayoutType.MINIMAL_LIST -> {
                        val listState = rememberLazyListState()

                        PhotosList(
                            lazyPhotoItems = lazyPhotoItems,
                            onPhotoClicked = { id ->
                                onEvent(SearchEvent.SelectPhoto(id))
                            },
                            onUserProfileClicked = { nickname ->
                                onEvent(SearchEvent.SelectUser(nickname))
                            },
                            isCompact = true,
                            photosLoadQuality = uiState.photosLoadQuality,
                            listState = listState,
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding(),
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    PhotosListLayoutType.STAGGERED_GRID -> {
                        val gridState = rememberLazyStaggeredGridState()

                        PhotosGrid(
                            lazyPhotoItems = lazyPhotoItems,
                            onPhotoClicked = { id ->
                                onEvent(SearchEvent.SelectPhoto(id))
                            },
                            photosLoadQuality = uiState.photosLoadQuality,
                            gridState = gridState,
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding(),
                            ),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            SearchScreenTabs.Collections.ordinal -> {
                val lazyCollectionItems = uiState.collections.collectAsLazyPagingItems()

                when (uiState.collectionsLayoutType) {
                    CollectionListLayoutType.DEFAULT -> {
                        val listState = rememberLazyListState()

                        CollectionsList(
                            lazyCollectionItems = lazyCollectionItems,
                            onCollectionClicked = { id ->
                                onEvent(SearchEvent.SelectCollection(id))
                            },
                            onUserProfileClicked = { nickname ->
                                onEvent(SearchEvent.SelectUser(nickname))
                            },
                            onPhotoClicked = { id ->
                                onEvent(SearchEvent.SelectPhoto(id))
                            },
                            photosLoadQuality = uiState.photosLoadQuality,
                            listState = listState,
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding(),
                            )
                        )
                    }

                    CollectionListLayoutType.MINIMAL_LIST -> {
                        val listState = rememberLazyListState()

                        CollectionsList(
                            lazyCollectionItems = lazyCollectionItems,
                            onCollectionClicked = { id ->
                                onEvent(SearchEvent.SelectCollection(id))
                            },
                            onUserProfileClicked = { nickname ->
                                onEvent(SearchEvent.SelectUser(nickname))
                            },
                            onPhotoClicked = { id ->
                                onEvent(SearchEvent.SelectPhoto(id))
                            },
                            photosLoadQuality = uiState.photosLoadQuality,
                            isCompact = true,
                            listState = listState,
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding(),
                            )
                        )
                    }

                    CollectionListLayoutType.GRID -> {
                        val gridState = rememberLazyGridState()

                        CollectionsGrid(
                            lazyCollectionItems = lazyCollectionItems,
                            onCollectionClicked = { id ->
                                onEvent(SearchEvent.SelectCollection(id))
                            },
                            photosLoadQuality = uiState.photosLoadQuality,
                            gridState = gridState,
                            contentPadding = PaddingValues(
                                top = dimensionResource(id = R.dimen.list_top_padding),
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding(),
                            )
                        )
                    }
                }
            }

            SearchScreenTabs.Users.ordinal -> {
                val lazyUserItems = uiState.users.collectAsLazyPagingItems()

                val listState = rememberLazyListState()

                UsersList(
                    lazyUserItems = lazyUserItems,
                    onUserClick = { nickname ->
                        onEvent(SearchEvent.SelectUser(nickname))
                    },
                    listState = listState,
                    contentPadding = PaddingValues(
                        top = dimensionResource(id = R.dimen.list_top_padding),
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding(),
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

@Preview
@Composable
fun SearchScreenPreview() {
    WalleriaTheme {
        Surface {
            val state = SearchUiState()

            SearchScreen(state = state, onEvent = {})
        }
    }
}