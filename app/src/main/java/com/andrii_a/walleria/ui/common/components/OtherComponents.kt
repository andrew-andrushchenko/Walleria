package com.andrii_a.walleria.ui.common.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.placeholder
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.domain.models.user.User
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import com.andrii_a.walleria.ui.util.abbreviatedNumberString
import com.andrii_a.walleria.ui.util.userFullName
import kotlinx.coroutines.launch

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
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WLoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        LoadingIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun TagsRow(
    tags: List<Tag>,
    onTagClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    LazyRow(
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items(count = tags.size) { index ->
            val tag = tags[index]

            AssistChip(
                onClick = { onTagClicked(tag.title) },
                label = { Text(text = tag.title) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DisplayOptions(
    modifier: Modifier = Modifier,
    @StringRes optionsStringRes: List<Int>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        modifier = modifier
    ) {
        optionsStringRes.forEachIndexed { index, optionStringRes ->
            ToggleButton(
                checked = index == selectedOption,
                onCheckedChange = {
                    onOptionSelected(index)
                },
                modifier = Modifier.weight(1f).semantics { role = Role.RadioButton },
                colors = ToggleButtonDefaults.tonalToggleButtonColors(),
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        optionsStringRes.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    }
            ) {
                Text(
                    text = stringResource(id = optionStringRes),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
            ),
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
        val placeholderColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp)

        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(user?.profileImage?.medium)
                .crossfade(durationMillis = 1000)
                .placeholder(placeholderColor.toArgb().toDrawable())
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

@Preview
@Composable
private fun LoadingListBannerPreview() {
    WalleriaTheme {
        Surface {
            WLoadingIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}