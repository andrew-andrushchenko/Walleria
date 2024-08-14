package com.andrii_a.walleria.ui.common

interface UiError

class UiErrorWithRetry(
    val reason: UiText,
    val onRetry: () -> Unit = {}
) : UiError
