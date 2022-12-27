package com.andrii_a.walleria.ui.search

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.collections.CollectionsList
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import com.andrii_a.walleria.ui.photos.PhotosList
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SearchScreen(
    query: StateFlow<String>,
    photos: Flow<PagingData<Photo>>,
    collections: Flow<PagingData<Collection>>,
    users: Flow<PagingData<User>>,
    photoFilters: StateFlow<PhotoFilters>,
    dispatchEvent: (SearchScreenEvent) -> Unit
) {
    val pagerState = rememberPagerState()

    var showFilterDialog by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        val queryValue by query.collectAsState()

        SearchRow(
            query = queryValue,
            pagerState = pagerState,
            dispatchEvent = dispatchEvent,
            onPhotoFiltersClick = { showFilterDialog = true }
        )

        SearchTabs(pagerState = pagerState)

        SearchPages(
            pagerState = pagerState,
            photos = photos,
            collections = collections,
            users = users
        )

        if (showFilterDialog) {
            SearchPhotoFilterDialog(
                photoFilters = photoFilters.collectAsState(),
                onApplyClick = dispatchEvent,
                onDismiss = { showFilterDialog = false }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SearchRow(
    query: String,
    pagerState: PagerState,
    dispatchEvent: (SearchScreenEvent) -> Unit,
    onPhotoFiltersClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .statusBarsPadding()
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxWidth()
    ) {
        val focusManager = LocalFocusManager.current
        var text by remember { mutableStateOf(query) }

        OutlinedTextField(
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
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(
                        onClick = { text = "" }
                    ) {
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
                    dispatchEvent(SearchScreenEvent.OnQueryChanged(query = text))
                    focusManager.clearFocus()
                }
            ),
            textStyle = MaterialTheme.typography.subtitle1,
            modifier = Modifier.weight(2f)
        )

        AnimatedVisibility(visible = pagerState.currentPage == SearchScreenTabs.Photos.ordinal) {
            IconButton(onClick = onPhotoFiltersClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter_outlined),
                    contentDescription = stringResource(id = R.string.photo_filters),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SearchTabs(
    pagerState: PagerState
) {
    val coroutineScope = rememberCoroutineScope()

    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.9f),
        contentColor = MaterialTheme.colors.onPrimary,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    .height(4.dp)
                    .padding(horizontal = 28.dp)
                    .background(
                        color = MaterialTheme.colors.onPrimary,
                        shape = RoundedCornerShape(16.dp)
                    )
            )
        }
    ) {
        SearchScreenTabs.values().forEachIndexed { index, tabPage ->
            Tab(
                selected = index == pagerState.currentPage,
                onClick = {
                    coroutineScope.launch {
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

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SearchPages(
    pagerState: PagerState,
    photos: Flow<PagingData<Photo>>,
    collections: Flow<PagingData<Collection>>,
    users: Flow<PagingData<User>>
) {
    HorizontalPager(
        count = SearchScreenTabs.values().size,
        state = pagerState
    ) { index ->
        when (index) {
            SearchScreenTabs.Photos.ordinal -> {
                PhotosList(
                    pagingDataFlow = photos,
                    onPhotoClicked = {},
                    onUserProfileClicked = {},
                    contentPadding = PaddingValues(top = 8.dp, bottom = 160.dp)
                )
            }
            SearchScreenTabs.Collections.ordinal -> {
                CollectionsList(
                    pagingDataFlow = collections,
                    onCollectionClicked = {},
                    onUserProfileClicked = {},
                    onPhotoClicked = {},
                    contentPadding = PaddingValues(top = 8.dp, bottom = 160.dp)
                )
            }
            SearchScreenTabs.Users.ordinal -> {
                val listState = rememberLazyListState()

                ScrollToTopLayout(
                    listState = listState,
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    UsersList(
                        pagingDataFlow = users,
                        onUserClick = {},
                        contentPadding = PaddingValues(top = 8.dp, bottom = 160.dp),
                        listState = listState
                    )
                }
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