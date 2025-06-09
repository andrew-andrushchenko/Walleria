package com.andrii_a.walleria.ui.search

import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
import com.andrii_a.walleria.domain.SearchResultsContentFilter
import com.andrii_a.walleria.domain.SearchResultsDisplayOrder
import com.andrii_a.walleria.domain.SearchResultsPhotoColor
import com.andrii_a.walleria.domain.SearchResultsPhotoOrientation
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.iconRes
import com.andrii_a.walleria.ui.util.titleRes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchPhotoFiltersBottomSheet(
    photoFilters: PhotoFilters,
    onEvent: (SearchEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    onDismiss: () -> Unit
) {
    var order by rememberSaveable { mutableStateOf(photoFilters.order) }
    var contentFilter by rememberSaveable { mutableStateOf(photoFilters.contentFilter) }
    var color by rememberSaveable { mutableStateOf(photoFilters.color) }
    var orientation by rememberSaveable { mutableStateOf(photoFilters.orientation) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(id = R.string.order),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val options = SearchResultsDisplayOrder.entries

            options.forEachIndexed { index, displayOrder ->
                ToggleButton(
                    checked = displayOrder == order,
                    onCheckedChange = {
                        order = displayOrder
                    },
                    modifier = Modifier.weight(1f).semantics { role = Role.RadioButton },
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
            text = stringResource(id = R.string.content_filter),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val options = SearchResultsContentFilter.entries

            options.forEachIndexed { index, filter ->
                ToggleButton(
                    checked = contentFilter == filter,
                    onCheckedChange = {
                        contentFilter = filter
                    },
                    modifier = Modifier.weight(1f).semantics { role = Role.RadioButton },
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        }
                ) {
                    Text(
                        text = stringResource(filter.titleRes),
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
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            val options = SearchResultsPhotoOrientation.entries

            options.forEachIndexed { index, searchResultsPhotoOrientation ->
                ToggleButton(
                    checked = orientation == searchResultsPhotoOrientation,
                    onCheckedChange = { orientation = searchResultsPhotoOrientation },
                    modifier = Modifier.weight(1f).semantics { role = Role.RadioButton },
                    colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        }
                ) {
                    if (index == 0) {
                        Text(
                            text = stringResource(id = searchResultsPhotoOrientation.titleRes),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                searchResultsPhotoOrientation.iconRes
                            ),
                            contentDescription = null
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.filter_color),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val listState = rememberLazyListState()

        LaunchedEffect(key1 = Unit) {
            listState.animateScrollToItem(color.ordinal)
        }

        PhotoColorsList(
            selectedOption = color,
            onColorItemSelected = { color = it },
            listState = listState,
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val filters = PhotoFilters(
                    order = order,
                    contentFilter = contentFilter,
                    color = color,
                    orientation = orientation
                )

                onEvent(SearchEvent.ChangePhotoFilters(filters))
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.apply),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun PhotoColorsList(
    selectedOption: SearchResultsPhotoColor,
    onColorItemSelected: (SearchResultsPhotoColor) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    listState: LazyListState = rememberLazyListState()
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = contentPadding,
        state = listState,
        modifier = modifier.selectableGroup()
    ) {
        items(SearchResultsPhotoColor.entries) {
            PresetColorItem(
                colorItem = it,
                selected = it == selectedOption,
                onSelected = { onColorItemSelected(it) },
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun PresetColorItem(
    modifier: Modifier = Modifier,
    colorItem: SearchResultsPhotoColor,
    selected: Boolean,
    onSelected: () -> Unit
) {
    val borderWidth by animateDpAsState(targetValue = if (selected) 2.dp else 0.dp)

    Surface(
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (selected) BorderStroke(
            width = borderWidth,
            MaterialTheme.colorScheme.outline
        ) else null,
        onClick = onSelected,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            content = {},
            modifier = Modifier
                .size(72.dp)
                .padding(12.dp)
                .drawBehind {
                    drawColorCircle(colorItem)
                }
        )
    }
}

private fun DrawScope.drawColorCircle(colorItem: SearchResultsPhotoColor) {
    when (colorItem) {
        SearchResultsPhotoColor.ANY -> {
            val side1 = 100
            val side2 = 200

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFF36870),
                        Color(0xFFFF9636),
                        Color(0xFFF8EA8C),
                    ),
                    center = Offset(side1 / 2.0f, side2 / 2.0f),
                    radius = side1 / 2.0f,
                    tileMode = TileMode.Repeated,
                ),
                style = Stroke(10.dp.toPx())
            )
        }

        SearchResultsPhotoColor.BLACK_AND_WHITE -> {
            drawArc(
                color = Color(0xFFFDFDFD),
                startAngle = -90f,
                sweepAngle = 180f,
                useCenter = true,
            )

            drawArc(
                color = Color(0xFF252930),
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true,
            )
        }

        SearchResultsPhotoColor.BLACK -> {
            drawCircle(Color(0xFF252930))
            drawCircle(
                Color(0xFFFDFDFD),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        SearchResultsPhotoColor.WHITE -> {
            drawCircle(Color(0xFFFDFDFD))
            drawCircle(
                Color(0xFF252930),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        SearchResultsPhotoColor.YELLOW -> {
            drawCircle(Color(0xFFF8EA8C))
        }

        SearchResultsPhotoColor.ORANGE -> {
            drawCircle(Color(0xFFFF9636))
        }

        SearchResultsPhotoColor.RED -> {
            drawCircle(Color(0xFFF36870))
        }

        SearchResultsPhotoColor.PURPLE -> {
            drawCircle(Color(0xFFC55FFC))
        }

        SearchResultsPhotoColor.MAGENTA -> {
            drawCircle(Color(0xFFFF0BAC))
        }

        SearchResultsPhotoColor.GREEN -> {
            drawCircle(Color(0xFF7CB46B))
        }

        SearchResultsPhotoColor.TEAL -> {
            drawCircle(Color(0xFF66B2B2))
        }

        SearchResultsPhotoColor.BLUE -> {
            drawCircle(Color(0xFF005B96))
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchPhotoFiltersBottomSheetPreview() {
    WalleriaTheme {
        Surface {
            SearchPhotoFiltersBottomSheet(
                photoFilters = PhotoFilters(
                    order = SearchResultsDisplayOrder.RELEVANT,
                    contentFilter = SearchResultsContentFilter.LOW,
                    color = SearchResultsPhotoColor.ANY,
                    orientation = SearchResultsPhotoOrientation.ANY
                ),
                onEvent = {},
                onDismiss = {}
            )
        }
    }
}