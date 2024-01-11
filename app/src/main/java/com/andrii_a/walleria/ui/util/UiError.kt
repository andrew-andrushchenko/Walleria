package com.andrii_a.walleria.ui.util

data class UiError(
    val reason: UiText,
    val onRetry: () -> Unit = {}
)
