package com.andrii_a.walleria.ui.search

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.ScreenOffset
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.search.SearchHistoryItem
import com.andrii_a.walleria.ui.common.components.CollectionsGridContent
import com.andrii_a.walleria.ui.common.components.PhotosGridContent
import com.andrii_a.walleria.ui.common.components.UsersGridContent
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    state: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = 0) { SearchScreenTabs.entries.size }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val textFieldState = rememberTextFieldState(
        initialText = state.query
    )
    val searchBarState = rememberSearchBarState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = SearchBarDefaults.enterAlwaysSearchBarScrollBehavior()

    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                modifier = Modifier,
                searchBarState = searchBarState,
                textFieldState = textFieldState,
                onSearch = {
                    scope.launch { searchBarState.animateToCollapsed() }
                    onEvent(SearchEvent.ChangeQuery(textFieldState.text.toString()))
                    onEvent(SearchEvent.PerformSearch)
                },
                placeholder = { Text(stringResource(id = R.string.type_something)) },
                leadingIcon = {
                    AnimatedContent(
                        targetState = searchBarState.currentValue == SearchBarValue.Expanded,
                        label = ""
                    ) { isExpanded ->
                        if (isExpanded) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        searchBarState.animateToCollapsed()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = stringResource(id = R.string.navigate_back)
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { onEvent(SearchEvent.GoBack) }
                            ) {
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
                        visible = (pagerState.currentPage == SearchScreenTabs.Photos.ordinal) && searchBarState.currentValue == SearchBarValue.Collapsed,
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
        }

    val toolbarScrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
        exitDirection = FloatingToolbarExitDirection.Bottom
    )

    Scaffold(
        topBar = {
            AppBarWithSearch(
                scrollBehavior = scrollBehavior,
                state = searchBarState,
                inputField = inputField,
            )
            ExpandedFullScreenSearchBar(
                state = searchBarState,
                inputField = inputField,
            ) {
                if (state.searchHistory.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.search_history_empty_text),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.search_history),
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SearchHistoryList(
                        searchHistory = state.searchHistory,
                        onItemSelected = { item ->
                            scope.launch {
                                searchBarState.animateToCollapsed()
                            }
                            onEvent(SearchEvent.ChangeQuery(item.title))
                            onEvent(SearchEvent.PerformSearch)
                        },
                        onDeleteItem = { item ->
                            onEvent(SearchEvent.DeleteSearchHistoryItem(item))
                        },
                        onDeleteAllItems = {
                            onEvent(SearchEvent.DeleteSearchHistory)
                        }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .nestedScroll(toolbarScrollBehavior)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Pages(
                pagerState = pagerState,
                uiState = state,
                onEvent = onEvent,
                contentPadding = innerPadding
            )

            SearchToolbar(
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
    }


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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun SearchToolbar(
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
        val options = SearchScreenTabs.entries

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
    uiState: SearchUiState,
    onEvent: (SearchEvent) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { index ->
        when (index) {
            SearchScreenTabs.Photos.ordinal -> {
                val lazyPhotoItems by rememberUpdatedState(newValue = uiState.photos.collectAsLazyPagingItems())

                PhotosGridContent(
                    photoItems = lazyPhotoItems,
                    onPhotoClicked = { onEvent(SearchEvent.SelectPhoto(it)) },
                    contentPadding = contentPadding,
                    scrollToTopButtonEnabled = false,
                )
            }

            SearchScreenTabs.Collections.ordinal -> {
                val lazyCollectionItems by rememberUpdatedState(newValue = uiState.collections.collectAsLazyPagingItems())

                CollectionsGridContent(
                    collectionItems = lazyCollectionItems,
                    onCollectionClick = { onEvent(SearchEvent.SelectCollection(it)) },
                    contentPadding = contentPadding,
                    scrollToTopButtonEnabled = false
                )
            }

            SearchScreenTabs.Users.ordinal -> {
                val lazyUserItems by rememberUpdatedState(newValue = uiState.users.collectAsLazyPagingItems())

                UsersGridContent(
                    userItems = lazyUserItems,
                    onUserClick = { onEvent(SearchEvent.SelectUser(it)) },
                    contentPadding = contentPadding,
                    scrollToTopButtonEnabled = false
                )
            }

            else -> throw IllegalStateException("Tab screen was not declared!")
        }
    }
}

@Composable
private fun SearchHistoryList(
    searchHistory: List<SearchHistoryItem>,
    onItemSelected: (SearchHistoryItem) -> Unit,
    onDeleteItem: (SearchHistoryItem) -> Unit,
    onDeleteAllItems: () -> Unit
) {
    LazyColumn {
        items(
            count = searchHistory.size,
            key = { index -> searchHistory[index].id }
        ) { index ->
            val searchHistoryItem = searchHistory[index]

            ListItem(
                headlineContent = { Text(text = searchHistoryItem.title) },
                leadingContent = {
                    Icon(
                        Icons.Default.History,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    IconButton(onClick = { onDeleteItem(searchHistoryItem) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                },
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                modifier = Modifier
                    .clickable(onClick = { onItemSelected(searchHistoryItem) })
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            )
        }

        if (searchHistory.isNotEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    TextButton(
                        onClick = onDeleteAllItems,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text(text = stringResource(id = R.string.clear_history))
                    }
                }
            }
        }
    }
}

private enum class SearchScreenTabs(@field:StringRes val titleRes: Int) {
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