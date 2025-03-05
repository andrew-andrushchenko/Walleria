package com.andrii_a.walleria.ui.common.components

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

@Composable
fun NestedScrollLayout(
    modifier: Modifier = Modifier,
    state: NestedScrollLayoutState,
    collapsableHeader: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState {
                    state.drag(it)
                }
            )
            .nestedScroll(state.nestedScrollConnectionHolder),
        content = {
            collapsableHeader()
            content()
        },
    ) { measurables, constraints ->
        val headerPlaceable =
            measurables[0].measure(constraints.copy(maxHeight = Constraints.Infinity))
        val contentPlaceable =
            measurables[1].measure(constraints.copy(maxHeight = constraints.maxHeight))

        layout(constraints.maxWidth, constraints.maxHeight) {
            headerPlaceable.place(0, state.offset.roundToInt())
            state.updateBounds(-(headerPlaceable.height.toFloat()))
            contentPlaceable.place(
                0,
                state.offset.roundToInt() + headerPlaceable.height
            )
        }
    }
}