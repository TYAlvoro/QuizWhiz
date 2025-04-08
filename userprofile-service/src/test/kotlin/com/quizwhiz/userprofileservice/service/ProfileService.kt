package com.quizwhiz.userprofileservice.service

import com.quizwhiz.userprofileservice.client.AuthServiceClient
import com.quizwhiz.userprofileservice.dto.UserDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class ProfileServiceTest {

    private val authServiceClient = mock<AuthServiceClient>()
    private val profileService = ProfileService(authServiceClient)

    @Test
    fun `getProfile should return ProfileDto from user data`() {
        val userDto = UserDto(
            id = 123,
            username = "alice",
            email = "alice@example.com",
            role = "TEACHER"
        )
        whenever(authServiceClient.getUserByUsername("alice")).thenReturn(userDto)
        val profile = profileService.getProfile("alice")
        assertEquals(123, profile.id)
        assertEquals("alice", profile.username)
        assertEquals("alice@example.com", profile.email)
        assertEquals("TEACHER", profile.role)
    }
}
