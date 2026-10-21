package com.andrii_a.walleria.data.remote.interceptor

import com.andrii_a.walleria.data.util.Config
import com.andrii_a.walleria.domain.repository.LocalAccountRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import okhttp3.Interceptor
import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthInterceptorTest {

    private lateinit var localAccountRepository: LocalAccountRepository
    private lateinit var authInterceptor: AuthInterceptor
    private lateinit var chain: Interceptor.Chain

    @BeforeEach
    fun setUp() {
        localAccountRepository = mockk()
        authInterceptor = AuthInterceptor(localAccountRepository)
        chain = mockk(relaxed = true)

        val initialRequest = Request.Builder()
            .url("https://api.unsplash.com/photos")
            .build()
        every { chain.request() } returns initialRequest
    }

    @Test
    fun `adds Bearer header when access token is present`() {
        val testToken = "valid_access_token"
        every { localAccountRepository.accessToken } returns flowOf(testToken)

        val requestSlot = slot<Request>()
        every { chain.proceed(capture(requestSlot)) } returns mockk()

        authInterceptor.intercept(chain)

        val capturedRequest = requestSlot.captured
        assertEquals("Bearer $testToken", capturedRequest.header("Authorization"))
    }

    @Test
    fun `adds Client-ID header when access token is null`() {
        every { localAccountRepository.accessToken } returns flowOf()

        val requestSlot = slot<Request>()
        every { chain.proceed(capture(requestSlot)) } returns mockk()

        authInterceptor.intercept(chain)

        val capturedRequest = requestSlot.captured
        assertEquals("Client-ID ${Config.CLIENT_ID}", capturedRequest.header("Authorization"))
    }

    @Test
    fun `adds Client-ID header when access token is blank`() {
        every { localAccountRepository.accessToken } returns flowOf("   ")

        val requestSlot = slot<Request>()
        every { chain.proceed(capture(requestSlot)) } returns mockk()

        authInterceptor.intercept(chain)

        val capturedRequest = requestSlot.captured
        assertEquals("Client-ID ${Config.CLIENT_ID}", capturedRequest.header("Authorization"))
    }
}