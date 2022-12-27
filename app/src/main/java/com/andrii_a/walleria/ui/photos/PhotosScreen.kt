package com.andrii_a.walleria.ui.photos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.andrii_a.walleria.R
import com.andrii_a.walleria.core.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.ScrollToTopLayout
import com.andrii_a.walleria.ui.common.WTitleDropdown
import com.andrii_a.walleria.ui.util.titleRes
import kotlinx.coroutines.flow.Flow

@Composable
fun PhotosScreen(
    photos: Flow<PagingData<Photo>>,
    order: PhotoListDisplayOrder,
    orderBy: (Int) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        val listState = rememberLazyListState()

        ScrollToTopLayout(
            listState = listState,
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            PhotosList(
                pagingDataFlow = photos,
                onPhotoClicked = {},
                onUserProfileClicked = {},
                listState = listState,
                contentPadding = PaddingValues(
                    top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding() + 48.dp
                )
            )
        }

        Row(
            modifier = Modifier
                .statusBarsPadding()
                .background(color = MaterialTheme.colors.primary.copy(alpha = 0.9f))
                .height(48.dp)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            val optionStringResources = PhotoListDisplayOrder.values().toList().map { it.titleRes }

            WTitleDropdown(
                selectedTitleRes = order.titleRes,
                titleTemplateRes = R.string.photos_title_template,
                optionsStringRes = optionStringResources,
                onItemSelected = orderBy
            )
        }
    }
}
