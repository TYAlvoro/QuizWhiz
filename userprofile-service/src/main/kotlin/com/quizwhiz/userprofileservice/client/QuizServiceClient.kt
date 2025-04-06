package com.quizwhiz.userprofileservice.client

import com.quizwhiz.userprofileservice.dto.CourseDto
import com.quizwhiz.userprofileservice.dto.RecentQuizAttemptDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(name = "quiz-service", url = "\${quizservice.url}")
interface QuizServiceClient {
    @GetMapping("/internal/courses/by-teacher/{teacherId}")
    fun getCoursesByTeacherId(@PathVariable teacherId: Long): List<CourseDto>

    @GetMapping("/internal/quiz-attempts/recent/{nickname}")
    fun getRecentQuizAttempts(@PathVariable nickname: String): List<RecentQuizAttemptDto>
}
