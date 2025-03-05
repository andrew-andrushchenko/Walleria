package com.andrii_a.walleria.ui.common.components

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.withSign

/**
 * Create a [NestedScrollLayoutState] that is remembered across compositions.
 */
@Composable
fun rememberNestedScrollLayoutState(): NestedScrollLayoutState {
    val scope = rememberCoroutineScope()
    val saver = remember {
        NestedScrollLayoutState.saver(scope = scope)
    }
    return rememberSaveable(
        saver = saver
    ) {
        NestedScrollLayoutState(scope = scope)
    }
}

/**
 * A state object that can be hoisted to observe scale and translate for [NestedScrollLayout].
 *
 * In most cases, this will be created via [rememberNestedScrollLayoutState].
 */
@Stable
class NestedScrollLayoutState(
    private val scope: CoroutineScope,
    initialOffset: Float = 0f,
    initialMaxOffset: Float = 0f,
) {
    companion object {
        fun saver(
            scope: CoroutineScope,
        ): Saver<NestedScrollLayoutState, *> = listSaver(
            save = {
                listOf(it.offset, it._maxOffset.floatValue)
            },
            restore = {
                NestedScrollLayoutState(
                    scope = scope,
                    initialOffset = it[0],
                    initialMaxOffset = it[1],
                )
            }
        )
    }

    /**
     * The current value for [NestedScrollLayout] Content translate
     */
    @get:FloatRange(from = 0.0)
    val offset: Float
        get() = _offset.value

    val nestedScrollConnectionHolder = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset = takeIf {
            available.y < 0 && source == NestedScrollSource.UserInput
        }?.let {
            Offset(0f, drag(available.y))
        } ?: Offset.Zero

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset = takeIf {
            available.y > 0 && source == NestedScrollSource.UserInput
        }?.let {
            Offset(0f, drag(available.y))
        } ?: Offset.Zero

        override suspend fun onPreFling(available: Velocity): Velocity =
            Velocity(0f, fling(available.y))

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
            Velocity(0f, fling(available.y))
    }

    private var changes = 0f
    private var _offset = Animatable(initialOffset)
    private val _maxOffset = mutableFloatStateOf(initialMaxOffset)

    private suspend fun snapTo(value: Float) {
        _offset.snapTo(value)
    }

    suspend fun fling(velocity: Float): Float {
        if (velocity == 0f || velocity > 0 && offset == 0f) {
            return velocity
        }
        val realVelocity = velocity.withSign(changes)
        changes = 0f
        return if (offset > _maxOffset.floatValue && offset <= 0) {
            _offset.animateDecay(
                realVelocity,
                exponentialDecay()
            ).endState.velocity.let {
                if (offset == 0f) {
                    velocity
                } else {
                    it
                }
            }
        } else {
            0f
        }
    }

    fun drag(delta: Float): Float =
        if (delta < 0 && offset > _maxOffset.floatValue || delta > 0 && offset < 0f) {
            changes = delta
            scope.launch {
                snapTo((offset + delta).coerceIn(_maxOffset.floatValue, 0f))
            }
            delta
        } else {
            0f
        }

    fun updateBounds(maxOffset: Float) {
        _maxOffset.floatValue = maxOffset
        _offset.updateBounds(maxOffset, 0f)
    }
}