package com.andrii_a.walleria.ui.search

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.collections.CollectionsList
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import com.andrii_a.walleria.ui.common.WOutlinedTextField
import com.andrii_a.walleria.ui.photos.PhotosList
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    query: StateFlow<String>,
    photos: Flow<PagingData<Photo>>,
    collections: Flow<PagingData<Collection>>,
    users: Flow<PagingData<User>>,
    photoFilters: StateFlow<PhotoFilters>,
    onEvent: (SearchScreenEvent) -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateBack: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0) { SearchScreenTabs.values().size }

    var showFilterDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxWidth()
    ) {
        val queryValue = query.collectAsStateWithLifecycle()

        TopBar(
            query = queryValue.value,
            pagerState = pagerState,
            onEvent = onEvent,
            onPhotoFiltersClick = { showFilterDialog = true },
            onNavigateBack = navigateBack
        )

        Pages(
            query = queryValue,
            pagerState = pagerState,
            photos = photos,
            collections = collections,
            users = users,
            navigateToPhotoDetails = navigateToPhotoDetails,
            navigateToCollectionDetails = navigateToCollectionDetails
        )
    }

    if (showFilterDialog) {
        SearchPhotoFilterDialog(
            photoFilters = photoFilters.collectAsStateWithLifecycle(),
            onApplyClick = onEvent,
            onDismiss = { showFilterDialog = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar(
    query: String,
    pagerState: PagerState,
    onEvent: (SearchScreenEvent) -> Unit,
    onPhotoFiltersClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface {
        ConstraintLayout(modifier = modifier.fillMaxWidth()) {
            val scope = rememberCoroutineScope()

            val (backButton, queryTextField, filterButton, tabs) = createRefs()

            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.constrainAs(backButton) {
                    top.linkTo(parent.top)
                    bottom.linkTo(tabs.top)
                    start.linkTo(parent.start, 16.dp)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = stringResource(id = R.string.photo_filters)
                )
            }

            val focusManager = LocalFocusManager.current
            var text by rememberSaveable { mutableStateOf(query) }

            WOutlinedTextField(
                value = text,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.type_something),
                        style = MaterialTheme.typography.subtitle1
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.Transparent,
                    cursorColor = MaterialTheme.colors.onPrimary,
                    disabledLabelColor = Color.LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = MaterialTheme.colors.onPrimary
                ),
                onValueChange = { text = it },
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                trailingIcon = {
                    if (text.isNotEmpty()) {
                        IconButton(onClick = { text = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.clear_search_field)
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        onEvent(SearchScreenEvent.OnQueryChanged(query = text))
                        focusManager.clearFocus()
                    }
                ),
                textStyle = MaterialTheme.typography.subtitle1,
                modifier = Modifier.constrainAs(queryTextField) {
                    top.linkTo(backButton.top)
                    bottom.linkTo(backButton.bottom)
                    start.linkTo(backButton.end)
                    if (pagerState.currentPage == SearchScreenTabs.Photos.ordinal) {
                        end.linkTo(filterButton.start)
                    } else {
                        end.linkTo(parent.end, 8.dp)
                    }
                    width = Dimension.fillToConstraints
                }
            )

            AnimatedVisibility(
                visible = pagerState.currentPage == SearchScreenTabs.Photos.ordinal,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier.constrainAs(filterButton) {
                    top.linkTo(queryTextField.top)
                    bottom.linkTo(queryTextField.bottom)
                    end.linkTo(parent.end, 16.dp)
                }
            ) {
                IconButton(onClick = onPhotoFiltersClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter_outlined),
                        contentDescription = stringResource(id = R.string.photo_filters)
                    )
                }
            }

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
                },
                modifier = Modifier
                    .constrainAs(tabs) {
                        top.linkTo(queryTextField.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
            ) {
                SearchScreenTabs.values().forEachIndexed { index, tabPage ->
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
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun Pages(
    query: State<String>,
    pagerState: PagerState,
    photos: Flow<PagingData<Photo>>,
    collections: Flow<PagingData<Collection>>,
    users: Flow<PagingData<User>>,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit
) {
    HorizontalPager(
        state = pagerState,
        contentPadding = contentPadding
    ) { index ->
        when (index) {
            SearchScreenTabs.Photos.ordinal -> {
                val lazyPhotoItems = photos.collectAsLazyPagingItems()

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = query.value.isNotEmpty() && lazyPhotoItems.loadState.refresh is LoadState.Loading,
                    onRefresh = lazyPhotoItems::refresh
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pullRefresh(pullRefreshState)
                ) {
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

                    PullRefreshIndicator(
                        refreshing = query.value.isNotEmpty() && lazyPhotoItems.loadState.refresh is LoadState.Loading,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }
            }

            SearchScreenTabs.Collections.ordinal -> {
                val lazyCollectionItems = collections.collectAsLazyPagingItems()

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = query.value.isNotEmpty() && lazyCollectionItems.loadState.refresh is LoadState.Loading,
                    onRefresh = lazyCollectionItems::refresh
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pullRefresh(pullRefreshState)
                ) {
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
                            onPhotoClicked = {},
                            listState = listState,
                            contentPadding = PaddingValues(
                                top = 8.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 64.dp
                            )
                        )

                        PullRefreshIndicator(
                            refreshing = query.value.isNotEmpty() && lazyCollectionItems.loadState.refresh is LoadState.Loading,
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
            }

            SearchScreenTabs.Users.ordinal -> {
                val lazyUserItems = users.collectAsLazyPagingItems()

                val pullRefreshState = rememberPullRefreshState(
                    refreshing = query.value.isNotEmpty() && lazyUserItems.loadState.refresh is LoadState.Loading,
                    onRefresh = lazyUserItems::refresh
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pullRefresh(pullRefreshState)
                ) {
                    val listState = rememberLazyListState()

                    ScrollToTopLayout(
                        listState = listState,
                        contentPadding = PaddingValues(
                            bottom = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding() + 8.dp
                        )
                    ) {
                        UsersList(
                            lazyUserItems = lazyUserItems,
                            onUserClick = {},
                            listState = listState,
                            contentPadding = PaddingValues(
                                top = 8.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 64.dp
                            )
                        )

                        PullRefreshIndicator(
                            refreshing = query.value.isNotEmpty() && lazyUserItems.loadState.refresh is LoadState.Loading,
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
            }

            else -> throw IllegalStateException("Tab screen was not declared!")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TopBarPreview() {
    WalleriaTheme {
        TopBar(
            query = "",
            pagerState = rememberPagerState(initialPage = 0) { SearchScreenTabs.values().size },
            onEvent = {},
            onPhotoFiltersClick = {},
            onNavigateBack = {}
        )
    }
}

private enum class SearchScreenTabs(@StringRes val titleRes: Int) {
    Photos(R.string.photos),
    Collections(R.string.collections),
    Users(R.string.users)
}