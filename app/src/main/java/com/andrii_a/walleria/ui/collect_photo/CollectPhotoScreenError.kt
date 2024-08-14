package com.andrii_a.walleria.ui.collect_photo

import com.andrii_a.walleria.ui.common.UiError
import com.andrii_a.walleria.ui.common.UiText

data class ListLoadingError(val reason: UiText) : UiError

data class CollectOperationError(val reason: UiText) : UiError