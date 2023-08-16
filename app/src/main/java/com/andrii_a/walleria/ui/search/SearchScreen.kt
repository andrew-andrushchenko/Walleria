package com.andrii_a.walleria.ui.search

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
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
    query: StateFlow<String>,
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
    val pagerState = rememberPagerState(initialPage = 0) { SearchScreenTabs.values().size }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }

    val filters by photoFilters.collectAsStateWithLifecycle()

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
            onPhotoFiltersClick = { openBottomSheet = !openBottomSheet },
            onNavigateBack = navigateBack
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
private fun TopBar(
    query: String,
    pagerState: PagerState,
    onEvent: (SearchScreenEvent) -> Unit,
    onPhotoFiltersClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
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
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.photo_filters)
            )
        }

        val focusManager = LocalFocusManager.current
        var text by rememberSaveable { mutableStateOf(query) }

        OutlinedTextField(
            value = text,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.type_something),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            onValueChange = { text = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,

            ),
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
                    onEvent(SearchScreenEvent.ChangeQuery(query = text))
                    focusManager.clearFocus()
                }
            ),
            textStyle = MaterialTheme.typography.titleMedium,
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
                    imageVector = Icons.Outlined.FilterList,
                    contentDescription = stringResource(id = R.string.photo_filters)
                )
            }
        }

        SearchTabs(
            pagerState = pagerState,
            modifier = Modifier
                .constrainAs(tabs) {
                    top.linkTo(queryTextField.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )
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
                                top = 8.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 200.dp
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
                                top = 8.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 200.dp
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
                                top = 8.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 200.dp,
                                start = 8.dp,
                                end = 8.dp
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
                                top = 8.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 64.dp
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
                                top = 8.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 64.dp
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
                                top = 8.dp,
                                bottom = WindowInsets.navigationBars.asPaddingValues()
                                    .calculateBottomPadding() + 64.dp
                            ),
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
                        top = 8.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 64.dp
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