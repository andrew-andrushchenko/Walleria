package com.andrii_a.walleria.domain.network

sealed interface Resource<out T> {
    data object Empty : Resource<Nothing>

    data object Loading : Resource<Nothing>

    data class Success<out T>(val value: T) : Resource<T>

    data class Error(val code: Int? = null, val reason: String? = null) : Resource<Nothing> {

        constructor(exception: Exception) : this(reason = exception.message)

        fun asException(): Exception {
            return Exception("Code: $code; Reason: $reason")
        }
    }
}
