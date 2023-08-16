package com.andrii_a.walleria.ui.common.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

private const val ANIMATION_DURATION_MILLIS = 500

@Stable
interface SingleChoiceSelectorState {
    val selectedIndex: Float
    val startCornerPercent: Int
    val endCornerPercent: Int
    val textColors: List<Color>

    fun selectOption(scope: CoroutineScope, index: Int)
}

@Stable
class SingleChoiceSelectorStateImpl(
    options: List<SingleChoiceSelectorItem>,
    selectedOptionOrdinal: Int,
    private val selectedColor: Color,
    private val unselectedColor: Color,
) : SingleChoiceSelectorState {

    override val selectedIndex: Float
        get() = _selectedIndex.value

    override val startCornerPercent: Int
        get() = _startCornerPercent.value.toInt()

    override val endCornerPercent: Int
        get() = _endCornerPercent.value.toInt()

    override val textColors: List<Color>
        get() = _textColors.value

    private var _selectedIndex = Animatable(selectedOptionOrdinal.toFloat())

    private var _startCornerPercent = Animatable(
        if (selectedOptionOrdinal == 0) {
            50f
        } else {
            15f
        }
    )

    private var _endCornerPercent = Animatable(
        if (selectedOptionOrdinal == options.size - 1) {
            50f
        } else {
            15f
        }
    )

    private var _textColors: State<List<Color>> = derivedStateOf {
        List(numOptions) { index ->
            lerp(
                start = unselectedColor,
                stop = selectedColor,
                fraction = 1f - (((selectedIndex - index.toFloat()).absoluteValue).coerceAtMost(1f))
            )
        }
    }

    private val numOptions = options.size

    private val animationSpec = tween<Float>(
        durationMillis = ANIMATION_DURATION_MILLIS,
        easing = FastOutSlowInEasing,
    )

    override fun selectOption(scope: CoroutineScope, index: Int) {
        scope.launch {
            _selectedIndex.animateTo(
                targetValue = index.toFloat(),
                animationSpec = animationSpec,
            )
        }

        scope.launch {
            _startCornerPercent.animateTo(
                targetValue = if (index == 0) 50f else 15f,
                animationSpec = animationSpec,
            )
        }

        scope.launch {
            _endCornerPercent.animateTo(
                targetValue = if (index == numOptions - 1) 50f else 15f,
                animationSpec = animationSpec,
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SingleChoiceSelectorStateImpl

        if (selectedColor != other.selectedColor) return false
        if (unselectedColor != other.unselectedColor) return false
        if (_selectedIndex != other._selectedIndex) return false
        if (_startCornerPercent != other._startCornerPercent) return false
        if (_endCornerPercent != other._endCornerPercent) return false
        if (numOptions != other.numOptions) return false
        if (animationSpec != other.animationSpec) return false

        return true
    }

    override fun hashCode(): Int {
        var result = selectedColor.hashCode()
        result = 31 * result + unselectedColor.hashCode()
        result = 31 * result + _selectedIndex.hashCode()
        result = 31 * result + _startCornerPercent.hashCode()
        result = 31 * result + _endCornerPercent.hashCode()
        result = 31 * result + numOptions
        result = 31 * result + animationSpec.hashCode()
        return result
    }
}

@Composable
fun rememberSingleChoiceSelectorState(
    options: List<SingleChoiceSelectorItem>,
    selectedOptionOrdinal: Int,
    selectedColor: Color,
    unSelectedColor: Color,
) = remember {
    SingleChoiceSelectorStateImpl(
        options,
        selectedOptionOrdinal,
        selectedColor,
        unSelectedColor,
    )
}

enum class SelectorItemType {
    IconAndText,
    IconOnly,
    TextOnly
}

data class SingleChoiceSelectorItem(
    @StringRes val titleRes: Int = 0,
    @DrawableRes val iconRes: Int = 0,
    val type: SelectorItemType = SelectorItemType.TextOnly
)

private enum class SelectorOption {
    Option,
    Background
}

@Composable
fun SingleChoiceSelector(
    options: List<SingleChoiceSelectorItem>,
    selectedOptionOrdinal: Int,
    onOptionSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = MaterialTheme.colorScheme.primaryContainer,
    unselectedColor: Color = MaterialTheme.colorScheme.primary,
    state: SingleChoiceSelectorState = rememberSingleChoiceSelectorState(
        options = options,
        selectedOptionOrdinal = selectedOptionOrdinal,
        selectedColor = selectedColor,
        unSelectedColor = unselectedColor,
    ),
) {
    require(options.size >= 2) { "This composable requires at least 2 options" }
    require(selectedOptionOrdinal < options.size) { "Invalid selected option [$selectedOptionOrdinal]" }

    LaunchedEffect(key1 = options, key2 = selectedOptionOrdinal) {
        state.selectOption(this, selectedOptionOrdinal)
    }

    Layout(
        modifier = modifier
            .clip(
                shape = RoundedCornerShape(percent = 50)
            )
            .background(Color.Transparent),
        content = {
            val colors = state.textColors

            options.forEachIndexed { index, option ->
                Box(
                    modifier = Modifier
                        .layoutId(SelectorOption.Option)
                        .clickable { onOptionSelect(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    when (option.type) {
                        SelectorItemType.IconAndText -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Icon(
                                    painter = painterResource(id = option.iconRes),
                                    tint = colors[index],
                                    contentDescription = null
                                )

                                Text(
                                    text = stringResource(id = option.titleRes),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = colors[index],
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 4.dp),
                                )
                            }
                        }

                        SelectorItemType.IconOnly -> {
                            Icon(
                                painter = painterResource(id = option.iconRes),
                                tint = colors[index],
                                contentDescription = null
                            )
                        }

                        SelectorItemType.TextOnly -> {
                            Text(
                                text = stringResource(id = option.titleRes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors[index],
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .layoutId(SelectorOption.Background)
                    .clip(
                        shape = RoundedCornerShape(
                            topStartPercent = state.startCornerPercent,
                            bottomStartPercent = state.startCornerPercent,
                            topEndPercent = state.endCornerPercent,
                            bottomEndPercent = state.endCornerPercent,
                        )
                    )
                    .background(MaterialTheme.colorScheme.primary),
            )
        }
    ) { measurables, constraints ->
        val optionWidth = constraints.maxWidth / options.size

        val optionConstraints = Constraints.fixed(
            width = optionWidth,
            height = constraints.maxHeight,
        )

        val optionPlaceables = measurables
            .filter { measurable -> measurable.layoutId == SelectorOption.Option }
            .map { measurable -> measurable.measure(optionConstraints) }

        val backgroundPlaceable = measurables
            .first { measurable -> measurable.layoutId == SelectorOption.Background }
            .measure(optionConstraints)

        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
        ) {
            backgroundPlaceable.placeRelative(
                x = (state.selectedIndex * optionWidth).toInt(),
                y = 0,
            )

            optionPlaceables.forEachIndexed { index, placeable ->
                placeable.placeRelative(
                    x = optionWidth * index,
                    y = 0,
                )
            }
        }
    }
}
