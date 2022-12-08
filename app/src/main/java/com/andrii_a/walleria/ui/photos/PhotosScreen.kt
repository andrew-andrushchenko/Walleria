package com.andrii_a.walleria.ui.photos

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.andrii_a.walleria.core.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.models.photo.Photo
import com.andrii_a.walleria.ui.common.SingleChoiceSelector
import kotlinx.coroutines.flow.Flow

@Composable
fun PhotosScreen(
    photos: Flow<PagingData<Photo>>,
    order: PhotoListDisplayOrder,
    orderBy: (Int) -> Unit
) {
    // List order selector height + top and bottom padding
    /*val singleChoiceSelectorHeight = 48.dp + (2 * 16).dp
    val singleChoiceSelectorHeightPx =
        with(LocalDensity.current) { singleChoiceSelectorHeight.roundToPx().toFloat() }

    val singleChoiceSelectorOffsetHeightPx = remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = singleChoiceSelectorOffsetHeightPx.value + delta
                singleChoiceSelectorOffsetHeightPx.value =
                    newOffset.coerceIn(-singleChoiceSelectorHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .nestedScroll(nestedScrollConnection)
    ) {
        PhotosList(
            pagingDataFlow = photos,
            onPhotoClicked = {},
            onUserProfileClicked = {},
            contentPadding = PaddingValues(top = singleChoiceSelectorHeight),
            modifier = Modifier.fillMaxWidth()
        )

        SingleChoiceSelector(
            options = PhotoListDisplayOrder.values().map { it.name },
            selectedOptionOrdinal = order.ordinal,
            onOptionSelect = orderBy,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp)
                .offset {
                    IntOffset(
                        x = 0,
                        y = singleChoiceSelectorOffsetHeightPx.value.roundToInt()
                    )
                }
        )
    }*/

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        SingleChoiceSelector(
            options = PhotoListDisplayOrder.values().map { it.name },
            selectedOptionOrdinal = order.ordinal,
            onOptionSelect = orderBy,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp)
        )

        PhotosList(
            pagingDataFlow = photos,
            onPhotoClicked = {},
            onUserProfileClicked = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}