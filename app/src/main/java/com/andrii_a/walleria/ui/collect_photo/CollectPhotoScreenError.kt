package com.andrii_a.walleria.ui.collect_photo

import com.andrii_a.walleria.ui.util.UiErr
import com.andrii_a.walleria.ui.util.UiText

data class ListLoadingError(val reason: UiText) : UiErr

data class CollectOperationError(val reason: UiText) : UiErr