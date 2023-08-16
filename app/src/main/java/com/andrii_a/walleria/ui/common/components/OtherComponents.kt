package com.andrii_a.walleria.ui.common.components

import android.graphics.drawable.ColorDrawable
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.userFullName
import kotlinx.coroutines.launch

@Composable
fun ScrollToTopLayout(
    listState: LazyListState,
    scrollToTopButtonPadding: PaddingValues,
    modifier: Modifier = Modifier,
    list: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        list()

        val showButton = remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }

        AnimatedVisibility(
            visible = showButton.value,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(scrollToTopButtonPadding)
        ) {
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        listState.scrollToItem(0)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = stringResource(id = R.string.to_top)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = stringResource(id = R.string.to_top))
            }
        }
    }
}

@Composable
fun ScrollToTopLayout(
    gridState: LazyStaggeredGridState,
    scrollToTopButtonPadding: PaddingValues,
    modifier: Modifier = Modifier,
    grid: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        grid()

        val showButton = remember {
            derivedStateOf {
                gridState.firstVisibleItemIndex > 0
            }
        }

        AnimatedVisibility(
            visible = showButton.value,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(scrollToTopButtonPadding)
        ) {
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        gridState.scrollToItem(0)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = stringResource(id = R.string.to_top)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = stringResource(id = R.string.to_top))
            }
        }
    }
}

@Composable
fun ScrollToTopLayout(
    gridState: LazyGridState,
    scrollToTopButtonPadding: PaddingValues,
    modifier: Modifier = Modifier,
    grid: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    Box(modifier = modifier) {
        grid()

        val showButton = remember {
            derivedStateOf {
                gridState.firstVisibleItemIndex > 0
            }
        }

        AnimatedVisibility(
            visible = showButton.value,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(scrollToTopButtonPadding)
        ) {
            FilledTonalButton(
                onClick = {
                    scope.launch {
                        gridState.scrollToItem(0)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = stringResource(id = R.string.to_top)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = stringResource(id = R.string.to_top))
            }
        }
    }
}

@Composable
fun TagsRow(
    tags: List<Tag>,
    onTagClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = modifier) {
        itemsIndexed(tags) { index, item ->
            AssistChip(
                onClick = { onTagClicked(item.title) },
                label = { Text(text = item.title) },
                modifier = modifier.padding(
                    start = if (index == 0) 8.dp else 0.dp,
                    end = 8.dp
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WTitleDropdown(
    @StringRes selectedTitleRes: Int,
    @StringRes titleTemplateRes: Int,
    @StringRes optionsStringRes: List<Int>,
    onItemSelected: (Int) -> Unit
) {
    var dropdownExpanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = {
            dropdownExpanded = !dropdownExpanded
        },
        modifier = Modifier.wrapContentWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.menuAnchor()
        ) {
            Text(
                text = stringResource(
                    id = titleTemplateRes,
                    stringResource(id = selectedTitleRes)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Icon(
                imageVector = if (dropdownExpanded) {
                    Icons.Default.ArrowDropUp
                } else {
                    Icons.Default.ArrowDropDown
                },
                contentDescription = null
            )
        }

        ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = {
                dropdownExpanded = false
            },
            modifier = Modifier.wrapContentWidth()
        ) {
            optionsStringRes.forEachIndexed { indexOrdinal, optionStringRes ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                id = titleTemplateRes,
                                stringResource(id = optionStringRes)
                            )
                        )
                    },
                    onClick = {
                        onItemSelected(indexOrdinal)
                        dropdownExpanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun CheckBoxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null // null recommended for accessibility with screenreaders
        )

        Text(
            text = labelText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun UserRowWithPhotoCount(
    user: User?,
    totalPhotos: Long,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(user?.profileImage?.medium)
                .crossfade(durationMillis = 1000)
                .placeholder(ColorDrawable(Color.Gray.toArgb()))
                .build(),
            contentScale = ContentScale.Crop,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
        )

        Text(
            text = stringResource(
                id = R.string.bullet_template,
                user?.userFullName.orEmpty(),
                totalPhotos.abbreviatedNumberString
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
