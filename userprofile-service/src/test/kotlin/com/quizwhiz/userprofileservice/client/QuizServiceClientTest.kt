package com.quizwhiz.userprofileservice.client

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.cloud.openfeign.FeignClient
import kotlin.reflect.full.findAnnotation

class QuizServiceClientTest {

    @Test
    fun `should have correct FeignClient annotation settings`() {
        val annotation = QuizServiceClient::class.findAnnotation<FeignClient>()
        assertEquals("quiz-service", annotation?.name)
        assertEquals("\${quizservice.url}", annotation?.url)
    }
}
