package com.andrii_a.walleria.ui.common

import androidx.annotation.FloatRange
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.core.os.ConfigurationCompat
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.andrii_a.walleria.R
import com.andrii_a.walleria.ui.navigation.NavigationScreen
import com.andrii_a.walleria.ui.theme.OnButtonDark
import com.andrii_a.walleria.ui.theme.OnButtonLight
import com.andrii_a.walleria.ui.theme.WalleriaTheme
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun WNavigationBar(
    navScreenItems: Array<NavigationScreen>,
    currentRoute: String,
    navigateToRoute: (NavigationScreen) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary.copy(alpha = 0.9f),
    contentColor: Color = MaterialTheme.colors.onPrimary
) {
    val currentScreen = navScreenItems.first { it.route == currentRoute }

    Surface(
        color = color,
        contentColor = contentColor,
        modifier = modifier
    ) {
        val springSpec = SpringSpec<Float>(
            stiffness = 800f,
            dampingRatio = 0.8f
        )

        WNavigationBarLayout(
            selectedIndex = currentScreen.ordinal,
            itemCount = navScreenItems.size,
            indicator = { WBottomNavigationBarIndicator() },
            animSpec = springSpec,
            modifier = Modifier.navigationBarsPadding()
        ) {
            val configuration = LocalConfiguration.current
            val currentLocale: Locale =
                ConfigurationCompat.getLocales(configuration).get(0) ?: Locale.getDefault()

            navScreenItems.forEach { screen ->
                val selected = screen == currentScreen

                val text = stringResource(screen.titleRes).uppercase(currentLocale)

                WNavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = if (selected) screen.iconSelected else screen.iconUnselected),
                            tint = MaterialTheme.colors.onPrimary,
                            contentDescription = text
                        )
                    },
                    text = {
                        Text(
                            text = text,
                            color = MaterialTheme.colors.onPrimary,
                            style = MaterialTheme.typography.button,
                            maxLines = 1
                        )
                    },
                    selected = selected,
                    onSelected = { navigateToRoute(screen) },
                    animSpec = springSpec,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(18.dp))
                )
            }
        }
    }
}

@Composable
private fun WNavigationBarLayout(
    selectedIndex: Int,
    itemCount: Int,
    animSpec: AnimationSpec<Float>,
    indicator: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Track how "selected" each item is [0, 1]
    val selectionFractions = remember(itemCount) {
        List(itemCount) { i ->
            Animatable(if (i == selectedIndex) 1f else 0f)
        }
    }
    selectionFractions.forEachIndexed { index, selectionFraction ->
        val target = if (index == selectedIndex) 1f else 0f
        LaunchedEffect(target, animSpec) {
            selectionFraction.animateTo(target, animSpec)
        }
    }

    // Animate the position of the indicator
    val indicatorIndex = remember { Animatable(0f) }
    val targetIndicatorIndex = selectedIndex.toFloat()
    LaunchedEffect(targetIndicatorIndex) {
        indicatorIndex.animateTo(targetIndicatorIndex, animSpec)
    }

    Layout(
        modifier = modifier.height(dimensionResource(id = R.dimen.navigation_bar_height)),
        content = {
            content()
            Box(Modifier.layoutId("indicator"), content = indicator)
        }
    ) { measurables, constraints ->
        check(itemCount == (measurables.size - 1)) // account for indicator

        // Divide the width into n+1 slots and give the selected item 2 slots
        val unselectedWidth = constraints.maxWidth / (itemCount + 1)
        val selectedWidth = 2 * unselectedWidth
        val indicatorMeasurable = measurables.first { it.layoutId == "indicator" }

        val itemPlaceables = measurables
            .filterNot { it == indicatorMeasurable }
            .mapIndexed { index, measurable ->
                // Animate item's width based upon the selection amount
                val width = lerp(unselectedWidth, selectedWidth, selectionFractions[index].value)
                measurable.measure(
                    constraints.copy(
                        minWidth = width,
                        maxWidth = width
                    )
                )
            }
        val indicatorPlaceable = indicatorMeasurable.measure(
            constraints.copy(
                minWidth = selectedWidth,
                maxWidth = selectedWidth
            )
        )

        layout(
            width = constraints.maxWidth,
            height = itemPlaceables.maxByOrNull { it.height }?.height ?: 0
        ) {
            val indicatorLeft = indicatorIndex.value * unselectedWidth
            indicatorPlaceable.placeRelative(x = indicatorLeft.toInt(), y = 0)
            var x = 0
            itemPlaceables.forEach { placeable ->
                placeable.placeRelative(x = x, y = 0)
                x += placeable.width
            }
        }
    }
}

@Composable
fun WNavigationBarItem(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    selected: Boolean,
    onSelected: () -> Unit,
    animSpec: AnimationSpec<Float>,
    modifier: Modifier = Modifier
) {
    // Animate the icon/text positions within the item based on selection
    val animationProgress by animateFloatAsState(if (selected) 1f else 0f, animSpec, label = "")

    WNavigationBarItemLayout(
        icon = icon,
        text = text,
        animationProgress = animationProgress,
        modifier = modifier
            .selectable(selected = selected, onClick = onSelected)
            .wrapContentSize()
    )
}

@Composable
private fun WNavigationBarItemLayout(
    icon: @Composable BoxScope.() -> Unit,
    text: @Composable BoxScope.() -> Unit,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float,
    modifier: Modifier = Modifier
) {
    Layout(
        modifier = modifier,
        content = {
            Box(
                modifier = Modifier
                    .layoutId("icon")
                    .padding(horizontal = 2.dp),
                content = icon
            )
            val scale = lerp(0.6f, 1f, animationProgress)
            Box(
                modifier = Modifier
                    .layoutId("text")
                    .padding(horizontal = 2.dp)
                    .graphicsLayer {
                        alpha = animationProgress
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0f, 0.5f)
                    },
                content = text
            )
        }
    ) { measurables, constraints ->
        val iconPlaceable = measurables.first { it.layoutId == "icon" }.measure(constraints)
        val textPlaceable = measurables.first { it.layoutId == "text" }.measure(constraints)

        placeTextAndIcon(
            textPlaceable,
            iconPlaceable,
            constraints.maxWidth,
            constraints.maxHeight,
            animationProgress
        )
    }
}

private fun MeasureScope.placeTextAndIcon(
    textPlaceable: Placeable,
    iconPlaceable: Placeable,
    width: Int,
    height: Int,
    @FloatRange(from = 0.0, to = 1.0) animationProgress: Float
): MeasureResult {
    val iconY = (height - iconPlaceable.height) / 2
    val textY = (height - textPlaceable.height) / 2

    val textWidth = textPlaceable.width * animationProgress
    val iconX = (width - textWidth - iconPlaceable.width) / 2
    val textX = iconX + iconPlaceable.width

    return layout(width, height) {
        iconPlaceable.placeRelative(iconX.toInt(), iconY)
        if (animationProgress != 0f) {
            textPlaceable.placeRelative(textX.toInt(), textY)
        }
    }
}

@Composable
private fun WBottomNavigationBarIndicator(
    strokeWidth: Dp = 2.dp,
    color: Color = MaterialTheme.colors.onPrimary,
    shape: Shape = RoundedCornerShape(50)
) {
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .then(Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            .border(strokeWidth, color, shape)
    )
}

@Preview
@Composable
private fun WalleriaBottomNavPreview() {
    WalleriaTheme {
        WNavigationBar(
            navScreenItems = NavigationScreen.values(),
            currentRoute = NavigationScreen.Photos.route,
            navigateToRoute = { }
        )
    }
}

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
fun LoadingListItem(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.TopCenter) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_list_item_animation))

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = modifier
                .requiredSize(64.dp)
                .scale(2f, 2f)
        )
    }
}

@Composable
fun LoadingBanner(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_animation_light))

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE
        )
    }
}

@Composable
fun ErrorBanner(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.error_banner_text),
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(
                if (isSystemInDarkTheme()) R.raw.error_animation_dark
                else R.raw.error_animation_light
            )
        )

        LottieAnimation(
            composition = composition,
            iterations = Int.MAX_VALUE,
            modifier = Modifier
                .requiredSize(250.dp)
                .scale(1.3f, 1.3f)
        )

        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp)
        )

        Spacer(modifier = Modifier.padding(bottom = 8.dp))

        WButton(onClick = onRetry) {
            Text(text = stringResource(id = R.string.action_retry))
        }
    }
}

@Composable
fun ErrorItem(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.error_loading_items),
    onRetry: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        backgroundColor = MaterialTheme.colors.error,
        contentColor = MaterialTheme.colors.onError,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
        ) {
            Text(
                text = message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.6f)
            )

            WButton(
                onClick = onRetry,
                modifier = Modifier.weight(0.2f)
            ) {
                Text(text = stringResource(id = R.string.action_retry))
            }
        }
    }
}

@Preview
@Composable
fun ErrorItemPreview() {
    WalleriaTheme {
        ErrorItem(
            onRetry = {},
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
        )
    }
}

@Composable
fun EmptyContentBanner(
    modifier: Modifier = Modifier,
    message: String = stringResource(id = R.string.empty_content_banner_text)
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        val composition by rememberLottieComposition(
            spec = LottieCompositionSpec.RawRes(
                if (isSystemInDarkTheme()) R.raw.empty_animation_dark
                else R.raw.empty_animation_light
            )
        )

        LottieAnimation(
            composition = composition,
            iterations = 1,
            modifier = Modifier
                .requiredSize(250.dp)
                .scale(1.6f, 1.6f)
        )

        Text(
            text = message,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp)
        )
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
        textColor = MaterialTheme.colors.onBackground,
        backgroundColor = MaterialTheme.colors.background,
        focusedBorderColor = MaterialTheme.colors.onBackground,
        focusedLabelColor = MaterialTheme.colors.onBackground,
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
