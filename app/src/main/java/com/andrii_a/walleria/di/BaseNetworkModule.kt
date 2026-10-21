package com.andrii_a.walleria.di

import com.andrii_a.walleria.data.remote.interceptor.AuthInterceptor
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val baseNetworkModule = module {
    single<HttpClient> {
        provideHttpClient(get())
    }
}

private fun provideHttpClient(preferencesRepository: LocalAccountRepository): HttpClient {
    return HttpClient(OkHttp) {
        engine {
            config {
                addInterceptor(AuthInterceptor(preferencesRepository))
            }
        }

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
    }
}