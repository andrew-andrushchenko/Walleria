package com.andrii_a.walleria.ui.util

// TODO: replace with a simple interface and let specific modules define their own error implementations
class UiError(
    val reason: UiText,
    val onRetry: () -> Unit = {}
)

interface UiErr
