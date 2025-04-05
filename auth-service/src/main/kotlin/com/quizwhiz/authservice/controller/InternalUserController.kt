package com.quizwhiz.authservice.controller

import com.quizwhiz.authservice.dto.UserDto
import com.quizwhiz.authservice.model.User
import com.quizwhiz.authservice.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class InternalUserController(private val userRepository: UserRepository) {

    @GetMapping("/internal/users/by-username/{username}", produces = ["application/json"])
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<UserDto> {
        val user: User = userRepository.findByUsername(username)
            ?: return ResponseEntity.notFound().build()
        val userDto = UserDto(
            id = user.id,
            username = user.username,
            email = user.email,
            role = user.role
        )
        return ResponseEntity.ok(userDto)
    }
}
