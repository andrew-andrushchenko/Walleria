package com.andrii_a.walleria.ui.topic_details

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.ui.common.components.SelectorItemType
import com.andrii_a.walleria.ui.common.components.SingleChoiceSelector
import com.andrii_a.walleria.ui.common.components.SingleChoiceSelectorItem
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.iconRes
import com.andrii_a.walleria.ui.util.titleRes

@Composable
fun TopicPhotosFilterBottomSheet(
    topicPhotosFilters: TopicPhotosFilters,
    contentPadding: PaddingValues = PaddingValues(),
    onApplyClick: (TopicDetailsEvent) -> Unit,
    onDismiss: () -> Unit
) {
    var order by rememberSaveable { mutableStateOf(topicPhotosFilters.order) }
    var orientation by rememberSaveable { mutableStateOf(topicPhotosFilters.orientation) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.order),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSelector(
            options = PhotoListDisplayOrder.values().map {
                SingleChoiceSelectorItem(titleRes = it.titleRes)
            },
            selectedOptionOrdinal = order.ordinal,
            onOptionSelect = { orderOrdinal ->
                order = PhotoListDisplayOrder.values()[orderOrdinal]
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50)
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.orientation),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        SingleChoiceSelector(
            options = TopicPhotosOrientation.values().map {
                SingleChoiceSelectorItem(
                    titleRes = it.titleRes,
                    iconRes = it.iconRes,
                    type = SelectorItemType.IconOnly
                )
            },
            selectedOptionOrdinal = orientation.ordinal,
            onOptionSelect = { orderOrdinal ->
                orientation = TopicPhotosOrientation.values()[orderOrdinal]
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(50)
                )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val filters = TopicPhotosFilters(
                    order = order,
                    orientation = orientation
                )

                onApplyClick(TopicDetailsEvent.ChangeFilters(filters))
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.apply),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TopicPhotosFilterBottomSheetPreview() {
    WalleriaTheme {
        TopicPhotosFilterBottomSheet(
            topicPhotosFilters = TopicPhotosFilters(
                order = PhotoListDisplayOrder.LATEST,
                orientation = TopicPhotosOrientation.LANDSCAPE
            ),
            onApplyClick = {},
            onDismiss = {}
        )
    }
}