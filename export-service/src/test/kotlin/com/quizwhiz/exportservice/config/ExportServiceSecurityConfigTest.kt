package com.quizwhiz.exportservice.config

import com.quizwhiz.exportservice.security.JwtAuthenticationFilter
import com.quizwhiz.exportservice.security.JwtTokenProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class ExportServiceSecurityConfigTest {

    private val jwtTokenProvider = mock<JwtTokenProvider>()
    private val config = ExportServiceSecurityConfig(jwtTokenProvider)

    @Test
    fun `securityFilterChain should build without exceptions`() {
        // Мокаем HttpSecurity
        val http = mock<HttpSecurity>()
        whenever(http.csrf(any())).thenReturn(http)
        whenever(http.authorizeHttpRequests(any())).thenReturn(http)
        whenever(http.addFilterBefore(any(), any<Class<out UsernamePasswordAuthenticationFilter>>()))
            .thenReturn(http)

        // Мокаем build()
        val mockFilterChain = mock<DefaultSecurityFilterChain>()
        whenever(http.build()).thenReturn(mockFilterChain)

        val filterChain = config.securityFilterChain(http)
        assertNotNull(filterChain)
        assertEquals(mockFilterChain, filterChain)

        verify(http).addFilterBefore(
            any<JwtAuthenticationFilter>(),
            org.mockito.kotlin.eq(UsernamePasswordAuthenticationFilter::class.java)
        )
    }
}
