package com.quizwhiz.exportservice.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilterTest {
    private lateinit var jwtTokenProvider: JwtTokenProvider
    private lateinit var filter: JwtAuthenticationFilter

    @BeforeEach
    fun setUp() {
        jwtTokenProvider = mock()
        filter = JwtAuthenticationFilter(jwtTokenProvider)
        // Перед каждым тестом очищаем SecurityContextHolder
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should set authentication when valid token in header`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val filterChain = mock<FilterChain>()
        val token = "validToken"

        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("testuser")

        filter.doFilterInternal(request, response, filterChain)

        // Проверяем, что в SecurityContext появился authentication
        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth)
        assertEquals("testuser", auth.name)
        // Проверяем, что цепочка фильтров продолжилась
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should set authentication when valid token in cookies`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val filterChain = mock<FilterChain>()
        val token = "validCookieToken"

        val cookies = arrayOf(Cookie("JWT", token))
        whenever(request.cookies).thenReturn(cookies)
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("cookieUser")

        filter.doFilterInternal(request, response, filterChain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth)
        assertEquals("cookieUser", auth.name)
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should not set authentication if token is empty`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val filterChain = mock<FilterChain>()

        // Без заголовка "Authorization" и без cookies
        whenever(request.getHeader("Authorization")).thenReturn(null)
        whenever(request.cookies).thenReturn(null)

        filter.doFilterInternal(request, response, filterChain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNull(auth)
        verify(filterChain).doFilter(request, response)
    }

    @Test
    fun `should clear context if jwtTokenProvider throws exception`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val filterChain = mock<FilterChain>()
        val token = "invalid"

        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenThrow(RuntimeException("Invalid token"))

        filter.doFilterInternal(request, response, filterChain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNull(auth)
        verify(filterChain).doFilter(request, response)
    }
}
