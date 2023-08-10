package com.andrii_a.walleria.ui.common.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.models.common.Tag
import com.andrii_a.walleria.ui.theme.OnButtonDark
import com.andrii_a.walleria.ui.theme.OnButtonLight
import kotlinx.coroutines.launch

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
fun WButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = RoundedCornerShape(16.dp),
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary
    ),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        elevation = elevation,
        shape = shape,
        border = border,
        colors = colors,
        contentPadding = contentPadding,
        content = content
    )
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
fun TagItem(
    modifier: Modifier = Modifier,
    title: String,
    onClick: (String) -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colors.onPrimary
            )
            .clickable { onClick(title) }
            .padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
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
            TagItem(
                title = item.title,
                onClick = onTagClicked,
                modifier = Modifier.padding(
                    start = if (index == 0) 8.dp else 0.dp,
                    end = 8.dp
                )
            )
        }
    }
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
fun WOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors(
        textColor = if (isError) MaterialTheme.colors.error else MaterialTheme.colors.onBackground,
        backgroundColor = MaterialTheme.colors.background,
        cursorColor = MaterialTheme.colors.onBackground,
        focusedBorderColor = MaterialTheme.colors.onBackground,
        focusedLabelColor = MaterialTheme.colors.onBackground,
        leadingIconColor = MaterialTheme.colors.onBackground,
        trailingIconColor = MaterialTheme.colors.onBackground,
        unfocusedLabelColor = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
        unfocusedBorderColor = MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
        disabledLabelColor = Color.Gray,
        disabledBorderColor = Color.Gray,
        errorBorderColor = MaterialTheme.colors.error,
        errorLabelColor = MaterialTheme.colors.error,
        errorCursorColor = MaterialTheme.colors.error,
        errorLeadingIconColor = MaterialTheme.colors.error,
        errorTrailingIconColor = MaterialTheme.colors.error
    )
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors
    )
}

@Composable
fun CheckBoxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    labelText: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Checkbox
            )

    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = labelText)
    }
}
