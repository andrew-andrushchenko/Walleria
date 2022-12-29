package com.andrii_a.walleria.ui.common

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.theme.OnButtonDark
import com.andrii_a.walleria.ui.theme.OnButtonLight
import kotlinx.coroutines.launch

@Composable
fun WRegularAppBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    elevation: Dp = AppBarDefaults.TopAppBarElevation,
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentColor: Color = MaterialTheme.colors.onPrimary,
) {
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = navigationIcon,
        actions = actions,
        elevation = elevation,
        backgroundColor = backgroundColor,
        contentColor = contentColor
    )
}

@Composable
fun WTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iconPainter: Painter? = null,
    text: String,
    enabled: Boolean = true,
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = if (isSystemInDarkTheme()) OnButtonLight else OnButtonDark,
        disabledContentColor = Color.LightGray
    ),
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding
    ) {
        iconPainter?.let {
            Icon(
                painter = it,
                contentDescription = text
            )
        }

        Spacer(modifier = Modifier.padding(start = 4.dp, end = 4.dp))

        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun WTextButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector,
    text: String,
    enabled: Boolean = true,
    elevation: ButtonElevation? = null,
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(
        contentColor = if (isSystemInDarkTheme()) OnButtonLight else OnButtonDark
    ),
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = text
        )

        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ScrollToTopLayout(
    listState: LazyListState,
    contentPadding: PaddingValues,
    list: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    Box {
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
                .padding(contentPadding)
        ) {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = stringResource(id = R.string.to_top))
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_up_alt),
                        contentDescription = stringResource(id = R.string.to_top)
                    )
                },
                onClick = {
                    scope.launch {
                        listState.scrollToItem(0)
                    }
                }
            )
        }
    }
}

@Composable
fun CircleIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor = backgroundColor),
    contentDescription: String? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Surface(
            shape = CircleShape,
            elevation = 8.dp,
            color = backgroundColor
        ) {
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = icon,
                    contentDescription = contentDescription,
                    tint = contentColor,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun LoadingItem(indicatorColor: Color = MaterialTheme.colors.secondary) {
    CircularProgressIndicator(
        color = indicatorColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally)
    )
}

@OptIn(ExperimentalMaterialApi::class)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(
                    id = titleTemplateRes,
                    stringResource(id = selectedTitleRes)
                ),
                style = MaterialTheme.typography.h6,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Icon(
                painter = painterResource(id = if (dropdownExpanded) R.drawable.ic_arrow_up_alt else R.drawable.ic_arrow_down_alt),
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
                    onClick = {
                        onItemSelected(indexOrdinal)
                        dropdownExpanded = false
                    }
                ) {
                    Text(
                        text = stringResource(
                            id = titleTemplateRes,
                            stringResource(id = optionStringRes)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    indicatorColor: Color = MaterialTheme.colors.onPrimary
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator(color = indicatorColor)
    }
}
