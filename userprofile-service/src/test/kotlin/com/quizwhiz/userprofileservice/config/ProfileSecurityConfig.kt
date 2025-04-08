package com.quizwhiz.userprofileservice.config

import com.quizwhiz.userprofileservice.config.JwtTokenProvider
import com.quizwhiz.userprofileservice.security.JwtAuthenticationFilter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

class ProfileSecurityConfigTest {

    private val jwtTokenProvider = mock<JwtTokenProvider>()
    private val config = ProfileSecurityConfig(jwtTokenProvider)

    @Test
    fun `securityFilterChain should build correctly`() {
        val http = mock<HttpSecurity>()
        // Настраиваем DSL‑методы так, чтобы они возвращали один и тот же http
        whenever(http.csrf(any())).thenReturn(http)
        whenever(http.authorizeHttpRequests(any())).thenReturn(http)
        whenever(http.addFilterBefore(any(), eq(UsernamePasswordAuthenticationFilter::class.java))).thenReturn(http)
        val mockChain = mock<DefaultSecurityFilterChain>()
        whenever(http.build()).thenReturn(mockChain)

        val chain = config.securityFilterChain(http)
        assertNotNull(chain)
        assertEquals(mockChain, chain)

        verify(http).addFilterBefore(any<JwtAuthenticationFilter>(), eq(UsernamePasswordAuthenticationFilter::class.java))
    }
}
