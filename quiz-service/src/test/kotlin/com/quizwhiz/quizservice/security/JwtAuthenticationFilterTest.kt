package com.quizwhiz.quizservice.security

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
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `should set authentication when valid token in header`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val chain = mock<FilterChain>()
        val token = "validToken"

        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("testuser")

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth)
        assertEquals("testuser", auth.name)
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `should set authentication when valid token in cookies`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val chain = mock<FilterChain>()
        val token = "cookieToken"

        whenever(request.cookies).thenReturn(arrayOf(Cookie("JWT", token)))
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn("cookieUser")

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNotNull(auth)
        assertEquals("cookieUser", auth.name)
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `should not set authentication if no token found`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val chain = mock<FilterChain>()

        filter.doFilter(request, response, chain)

        val auth = SecurityContextHolder.getContext().authentication
        assertNull(auth)
        verify(chain).doFilter(request, response)
    }

    @Test
    fun `should clear context on exception`() {
        val request = mock<HttpServletRequest>()
        val response = mock<HttpServletResponse>()
        val chain = mock<FilterChain>()
        val token = "badToken"

        whenever(request.getHeader("Authorization")).thenReturn("Bearer $token")
        whenever(jwtTokenProvider.getUsernameFromJWT(token)).thenThrow(RuntimeException("Invalid token"))

        filter.doFilter(request, response, chain)

        assertNull(SecurityContextHolder.getContext().authentication)
        verify(chain).doFilter(request, response)
    }
}
