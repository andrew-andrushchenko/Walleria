package com.andrii_a.walleria.ui.search

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.common.components.CollectionsGridContent
import com.andrii_a.walleria.ui.common.components.PhotosGridContent
import com.andrii_a.walleria.ui.common.components.UsersGridContent
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

    Surface(shape = RoundedCornerShape(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .semantics { isTraversalGroup = true }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = state.query,
                            onQueryChange = { onEvent(SearchEvent.ChangeQuery(it)) },
                            onSearch = {
                                onEvent(SearchEvent.ToggleSearchBox(isExpanded = false))
                                onEvent(SearchEvent.PerformSearch)
                            },
                            expanded = state.isSearchBoxExpanded,
                            onExpandedChange = { onEvent(SearchEvent.ToggleSearchBox(isExpanded = it)) },
                            placeholder = { Text(stringResource(id = R.string.type_something)) },
                            leadingIcon = {
                                AnimatedContent(
                                    targetState = state.isSearchBoxExpanded,
                                    label = ""
                                ) { isExpanded ->
                                    if (isExpanded) {
                                        IconButton(onClick = {
                                            onEvent(
                                                SearchEvent.ToggleSearchBox(
                                                    isExpanded = false
                                                )
                                            )
                                        }) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = stringResource(id = R.string.navigate_back)
                                            )
                                        }
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
                                    visible = (pagerState.currentPage == SearchScreenTabs.Photos.ordinal) && !state.isSearchBoxExpanded,
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
                        )
                    },
                    expanded = state.isSearchBoxExpanded,
                    onExpandedChange = { onEvent(SearchEvent.ToggleSearchBox(isExpanded = it)) },
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
                            onEvent(SearchEvent.ToggleSearchBox(isExpanded = false))
                            onEvent(SearchEvent.ChangeQuery(item.title))
                            onEvent(SearchEvent.PerformSearch)
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
    }

    val scope = rememberCoroutineScope()

    if (state.isFilterDialogOpened) {
        ModalBottomSheet(
            onDismissRequest = { onEvent(SearchEvent.DismissFilterDialog) },
            sheetState = bottomSheetState
        ) {
            SearchPhotoFiltersBottomSheet(
                photoFilters = state.photoFilters,
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
    val coroutineScope = rememberCoroutineScope()

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        val options = SearchScreenTabs.entries

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
                val lazyPhotoItems by rememberUpdatedState(newValue = uiState.photos.collectAsLazyPagingItems())

                PhotosGridContent(
                    photoItems = lazyPhotoItems,
                    onPhotoClicked = { onEvent(SearchEvent.SelectPhoto(it)) },
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
                )
            }

            SearchScreenTabs.Collections.ordinal -> {
                val lazyCollectionItems by rememberUpdatedState(newValue = uiState.collections.collectAsLazyPagingItems())

                CollectionsGridContent(
                    collectionItems = lazyCollectionItems,
                    onCollectionClick = { onEvent(SearchEvent.SelectCollection(it)) },
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
                    )
                )
            }

            SearchScreenTabs.Users.ordinal -> {
                val lazyUserItems by rememberUpdatedState(newValue = uiState.users.collectAsLazyPagingItems())

                UsersGridContent(
                    userItems = lazyUserItems,
                    onUserClick = { onEvent(SearchEvent.SelectUser(it)) },
                    contentPadding = PaddingValues(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding(),
                    ),
                    scrollToTopButtonPadding = PaddingValues(
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()
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