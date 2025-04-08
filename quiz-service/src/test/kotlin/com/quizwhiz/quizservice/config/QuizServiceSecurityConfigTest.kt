package com.quizwhiz.quizservice.config

import com.quizwhiz.quizservice.security.JwtAuthenticationFilter
import com.quizwhiz.quizservice.security.JwtTokenProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class QuizServiceSecurityConfigTest {

    private val jwtTokenProvider = mock<JwtTokenProvider>()
    private val config = QuizServiceSecurityConfig(jwtTokenProvider)

    @Test
    fun `securityFilterChain should build without exceptions`() {
        // Мокаем HttpSecurity и методы DSL
        val http = mock<HttpSecurity>()
        whenever(http.csrf(any())).thenReturn(http)
        whenever(http.authorizeHttpRequests(any())).thenReturn(http)
        whenever(http.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter::class.java))).thenReturn(http)

        // Мокаем build()
        val mockChain = mock<DefaultSecurityFilterChain>()
        whenever(http.build()).thenReturn(mockChain)

        val chain = config.securityFilterChain(http)
        assertNotNull(chain)
        assertEquals(mockChain, chain)

        // Проверяем, что фильтр добавлен
        verify(http).addFilterBefore(any<JwtAuthenticationFilter>(), eq(UsernamePasswordAuthenticationFilter::class.java))
    }
}
