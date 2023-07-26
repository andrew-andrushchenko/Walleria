package com.andrii_a.walleria.data.util.network

import com.andrii_a.walleria.core.BackendResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

suspend fun <T> backendRequest(request: suspend () -> T): BackendResult<T> =
    withContext(Dispatchers.IO) {
        try {
            BackendResult.Success(request.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.errorBody
                    BackendResult.Error(code, errorResponse)
                }
                else -> BackendResult.Error(null, throwable.message)
            }
        }
    }

fun <T> backendRequestFlow(request: suspend () -> T): Flow<BackendResult<T>> = flow {
    emit(BackendResult.Loading)
    emit(backendRequest(request))
}

val HttpException.errorBody: String?
    get() = try {
        this.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        null
    }