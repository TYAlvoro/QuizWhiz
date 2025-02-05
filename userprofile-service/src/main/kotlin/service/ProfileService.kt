package service

import client.AuthServiceClient
import dto.ProfileDto
import org.springframework.stereotype.Service

@Service
class ProfileService(private val authServiceClient: AuthServiceClient) {

    fun getProfile(username: String): ProfileDto {
        // Получаем объект User из Auth Service через Feign
        val user = authServiceClient.getUserByUsername(username)
        return ProfileDto(
            username = user.username,
            email = user.email,
            role = user.role
        )
    }
}
