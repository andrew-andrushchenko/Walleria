package com.andrii_a.walleria.data.util.network

import com.andrii_a.walleria.domain.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

suspend fun <T> backendRequest(request: suspend () -> T): Resource<T> =
    withContext(Dispatchers.IO) {
        try {
            Resource.Success(request.invoke())
        } catch (throwable: Throwable) {
            coroutineContext.ensureActive()

            when (throwable) {
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = throwable.errorBody
                    Resource.Error(code, errorResponse)
                }
                else -> Resource.Error(null, throwable.message)
            }
        }
    }

fun <T> backendRequestFlow(request: suspend () -> T): Flow<Resource<T>> = flow {
    emit(Resource.Loading)
    emit(backendRequest(request))
}

val HttpException.errorBody: String?
    get() = try {
        this.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        null
    }