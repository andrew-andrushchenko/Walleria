package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.repository.UserAccountPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseNetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(preferencesRepository: UserAccountPreferencesRepository): HttpClient {
        return HttpClient(OkHttp) {
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.ANDROID
            }

            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                        explicitNulls = true
                    }
                )
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 30000
                requestTimeoutMillis = 30000
            }

            defaultRequest {
                val accessToken = runBlocking {
                    preferencesRepository.accessToken.firstOrNull()
                }

                if (!accessToken.isNullOrBlank()) {
                    header("Authorization", "Bearer $accessToken")
                } else {
                    header("Authorization", "Client-ID ${Config.CLIENT_ID}")
                }
            }
        }
    }
}