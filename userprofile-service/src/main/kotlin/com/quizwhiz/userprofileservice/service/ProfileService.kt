package com.quizwhiz.userprofileservice.service

import com.quizwhiz.userprofileservice.client.AuthServiceClient
import com.quizwhiz.userprofileservice.dto.ProfileDto
import org.springframework.stereotype.Service

@Service
class ProfileService(private val authServiceClient: AuthServiceClient) {
    fun getProfile(username: String): ProfileDto {
        val user = authServiceClient.getUserByUsername(username)
        return ProfileDto(
            id = user.id,
            username = user.username,
            email = user.email,
            role = user.role
        )
    }
}
