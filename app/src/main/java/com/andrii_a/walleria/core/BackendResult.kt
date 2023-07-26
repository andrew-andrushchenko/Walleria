package com.andrii_a.walleria.core

sealed interface BackendResult<out T> {
    data object Empty : BackendResult<Nothing>
    data object Loading : BackendResult<Nothing>
    data class Success<out T>(val value: T) : BackendResult<T>
    data class Error(val code: Int? = null, val reason: String? = null) : BackendResult<Nothing>
}
