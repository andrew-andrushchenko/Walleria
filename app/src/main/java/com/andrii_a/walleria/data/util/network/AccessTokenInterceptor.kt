package com.andrii_a.walleria.data.util.network

import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import com.andrii_a.walleria.data.util.CLIENT_ID

class AccessTokenInterceptor(
    private val userAccountPreferencesRepository: UserAccountPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            userAccountPreferencesRepository.accessToken.firstOrNull()
        }
        return if (accessToken.isNullOrBlank().not()) {
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            chain.proceed(authenticatedRequest)
        } else {
            val authenticatedRequest = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Client-ID $CLIENT_ID")
                .build()
            chain.proceed(authenticatedRequest)
        }
    }
}