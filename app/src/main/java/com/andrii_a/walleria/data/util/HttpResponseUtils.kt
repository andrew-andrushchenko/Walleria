package com.andrii_a.walleria.data.util

import com.andrii_a.walleria.domain.network.Resource
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

suspend inline fun <reified T> HttpResponse.asResource(): Resource<T> {
    val httpStatus = this.status

    return try {
        if (httpStatus.value in 200..299) {
            Resource.Success(this.body<T>())
        } else {
            Resource.Error(code = httpStatus.value, reason = httpStatus.description)
        }
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Resource.Error(code = httpStatus.value, reason = e.localizedMessage ?: "Network error")
    }
}

suspend inline fun <reified T> backendRequest(crossinline request: suspend () -> HttpResponse): Resource<T> {
    return try {
        request().asResource<T>()
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        Resource.Error(exception = e)
    }
}