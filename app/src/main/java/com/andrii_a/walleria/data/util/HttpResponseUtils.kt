package com.andrii_a.walleria.data.util

import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

suspend inline fun <reified T> HttpResponse.asResource(): Resource<T> {
    return when (val statusCode = this.status.value) {
        in 200..299 -> {
            val result = this.body<T>()
            Resource.Success(result)
        }
        401 -> Resource.Error(code = statusCode, reason = "Unauthorized")
        404 -> Resource.Error(code = statusCode, reason = "Not found")
        409 -> Resource.Error(code = statusCode, reason = "Conflict")
        408 -> Resource.Error(code = statusCode, reason = "Request timeout")
        413 -> Resource.Error(code = statusCode, reason = "Payload too large")
        in 500..599 -> Resource.Error(code = statusCode, reason = "Internal server error")
        else -> Resource.Error(code = statusCode, reason = "Unknown error")
    }
}

suspend inline fun <reified T> backendRequest(crossinline request: suspend () -> HttpResponse): Resource<T> {
    return try {
        request().asResource<T>()
    } catch (e: Exception) {
        coroutineContext.ensureActive()
        Resource.Error(exception = e)
    }
}