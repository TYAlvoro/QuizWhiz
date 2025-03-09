package com.quizwhiz.userprofileservice.client

import com.quizwhiz.userprofileservice.dto.CourseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "quiz-service", url = "http://\${quizservice.url}")
interface QuizServiceClient {

    // Предположим, что в quiz-service есть эндпоинт GET /internal/courses/by-teacher/{teacherId}
    @GetMapping("/internal/courses/by-teacher/{teacherId}")
    fun getCoursesByTeacherId(@PathVariable teacherId: Long): List<CourseDto>
}