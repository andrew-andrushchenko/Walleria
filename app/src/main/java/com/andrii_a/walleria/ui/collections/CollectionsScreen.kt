package com.andrii_a.walleria.ui.collections

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.CollectionListLayoutType
import com.andrii_a.walleria.domain.PhotoQuality
import com.andrii_a.walleria.domain.models.collection.Collection
import com.andrii_a.walleria.ui.common.CollectionId
import com.andrii_a.walleria.ui.common.PhotoId
import com.andrii_a.walleria.ui.common.SearchQuery
import com.andrii_a.walleria.ui.common.UserNickname
import com.andrii_a.walleria.ui.common.components.lists.CollectionsGrid
import com.andrii_a.walleria.ui.common.components.lists.CollectionsList
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    collections: Flow<PagingData<Collection>>,
    collectionsLayoutType: CollectionListLayoutType,
    photosLoadQuality: PhotoQuality,
    navigateToProfileScreen: () -> Unit,
    navigateToSearchScreen: (SearchQuery?) -> Unit,
    navigateToPhotoDetails: (PhotoId) -> Unit,
    navigateToCollectionDetails: (CollectionId) -> Unit,
    navigateToUserDetails: (UserNickname) -> Unit
) {
    val lazyCollectionItems = collections.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.all_collections),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    IconButton(onClick = { navigateToSearchScreen(null) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }

                    IconButton(onClick = navigateToProfileScreen) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = stringResource(
                                id = R.string.user_profile_image
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        when (collectionsLayoutType) {
            CollectionListLayoutType.DEFAULT -> {
                val listState = rememberLazyListState()

                CollectionsList(
                    lazyCollectionItems = lazyCollectionItems,
                    onCollectionClicked = navigateToCollectionDetails,
                    onUserProfileClicked = navigateToUserDetails,
                    onPhotoClicked = navigateToPhotoDetails,
                    isCompact = false,
                    addNavigationBarPadding = true,
                    listState = listState,
                    contentPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        top = innerPadding.calculateTopPadding() + dimensionResource(id = R.dimen.list_top_padding),
                        bottom = innerPadding.calculateBottomPadding() + dimensionResource(id = R.dimen.navigation_bar_height) * 2
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
                    isCompact = true,
                    addNavigationBarPadding = true,
                    photosLoadQuality = photosLoadQuality,
                    listState = listState,
                    contentPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        top = innerPadding.calculateTopPadding() + dimensionResource(id = R.dimen.list_top_padding),
                        bottom = innerPadding.calculateBottomPadding() + dimensionResource(id = R.dimen.navigation_bar_height) * 2
                    )
                )
            }

            CollectionListLayoutType.GRID -> {
                val gridState = rememberLazyGridState()

                CollectionsGrid(
                    lazyCollectionItems = lazyCollectionItems,
                    onCollectionClicked = navigateToCollectionDetails,
                    addNavigationBarPadding = true,
                    gridState = gridState,
                    photosLoadQuality = photosLoadQuality,
                    contentPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        top = innerPadding.calculateTopPadding() + dimensionResource(id = R.dimen.list_top_padding),
                        bottom = innerPadding.calculateBottomPadding() + dimensionResource(id = R.dimen.navigation_bar_height) * 2
                    )
                )
            }
        }
    }
}