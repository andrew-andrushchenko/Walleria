package com.andrii_a.walleria.data.remote.interceptor

import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val localAccountRepository: LocalAccountRepository) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            localAccountRepository.accessToken.firstOrNull()
        }

        val authHeaderValue = if (!accessToken.isNullOrBlank()) {
            "Bearer $accessToken"
        } else {
            "Client-ID ${Config.CLIENT_ID}"
        }

        val request = chain.request().newBuilder().apply {
            addHeader("Authorization", authHeaderValue)
        }.build()

        return chain.proceed(request)
    }
}