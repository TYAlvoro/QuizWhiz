package com.quizwhiz.exportservice.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtTokenProviderTest {

    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun setUp() {
        // Имитируем, будто прочитали секрет из @Value
        val secret = "ThisIsASecretKeyThatShouldBeAtLeast32Characters!"
        jwtTokenProvider = JwtTokenProvider(secret)
    }

    @Test
    fun `getUsernameFromJWT should return username when token is valid`() {
        val secretKey = Keys.hmacShaKeyFor("ThisIsASecretKeyThatShouldBeAtLeast32Characters!".toByteArray())
        val token = Jwts.builder()
            .claim("username", "testuser")
            .signWith(secretKey)
            .compact()

        val username = jwtTokenProvider.getUsernameFromJWT(token)
        assertEquals("testuser", username)
    }

    @Test
    fun `getUsernameFromJWT should throw if token is invalid`() {
        // Некорректная подпись или структура
        val invalidToken = "invalid.token.content"
        assertThrows(Exception::class.java) {
            jwtTokenProvider.getUsernameFromJWT(invalidToken)
        }
    }
}
