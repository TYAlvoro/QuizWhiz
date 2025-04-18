package com.quizwhiz.userprofileservice.client

import com.quizwhiz.userprofileservice.dto.UserDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "auth-service", url = "\${auth.service.url}")
interface AuthServiceClient {
    @GetMapping("/internal/users/by-username/{username}")
    fun getUserByUsername(@PathVariable username: String): UserDto
}
