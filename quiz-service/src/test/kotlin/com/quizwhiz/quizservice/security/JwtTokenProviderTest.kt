package com.quizwhiz.quizservice.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider
    private val secret = "MySecretKeyForJWTsWhichHasAtLeast32Bytes!"

    @BeforeEach
    fun setUp() {
        jwtTokenProvider = JwtTokenProvider(secret)
    }

    @Test
    fun `getUserIdFromJWT should return userId from token subject`() {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        val token = Jwts.builder()
            .setSubject("123")
            .signWith(key)
            .compact()

        val userId = jwtTokenProvider.getUserIdFromJWT(token)
        assertEquals(123, userId)
    }

    @Test
    fun `getUsernameFromJWT should return username from claims`() {
        val key = Keys.hmacShaKeyFor(secret.toByteArray())
        val token = Jwts.builder()
            .claim("username", "testUser")
            .signWith(key)
            .compact()

        val username = jwtTokenProvider.getUsernameFromJWT(token)
        assertEquals("testUser", username)
    }

    @Test
    fun `should throw if token is invalid`() {
        val invalidToken = "header.payload.signature"
        assertThrows(Exception::class.java) {
            jwtTokenProvider.getUserIdFromJWT(invalidToken)
        }
        assertThrows(Exception::class.java) {
            jwtTokenProvider.getUsernameFromJWT(invalidToken)
        }
    }
}
