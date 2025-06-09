package com.andrii_a.walleria.ui.topic_details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.TopicPhotosOrientation
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.iconRes
import com.andrii_a.walleria.ui.util.titleRes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            modifier = Modifier.fillMaxWidth()
        ) {
            val options = PhotoListDisplayOrder.entries

            options.forEachIndexed { index, displayOrder ->
                ToggleButton(
                    checked = displayOrder == order,
                    onCheckedChange = {
                        order = displayOrder
                    },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { role = Role.RadioButton },
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        }
                ) {
                    Text(
                        text = stringResource(displayOrder.titleRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.orientation),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            modifier = Modifier.fillMaxWidth()
        ) {
            val options = TopicPhotosOrientation.entries

            options.forEachIndexed { index, searchResultsPhotoOrientation ->
                ToggleButton(
                    checked = orientation == searchResultsPhotoOrientation,
                    onCheckedChange = { orientation = searchResultsPhotoOrientation },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { role = Role.RadioButton },
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        }
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            searchResultsPhotoOrientation.iconRes
                        ),
                        contentDescription = null
                    )
                }
            }
        }

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
        Surface {
            TopicPhotosFilterBottomSheet(
                topicPhotosFilters = TopicPhotosFilters(
                    order = PhotoListDisplayOrder.LATEST,
                    orientation = TopicPhotosOrientation.LANDSCAPE
                ),
                contentPadding = PaddingValues(16.dp),
                onApplyClick = {},
                onDismiss = {}
            )
        }
    }
}