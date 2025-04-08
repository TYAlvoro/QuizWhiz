package com.quizwhiz.userprofileservice.controller

import com.quizwhiz.userprofileservice.client.QuizServiceClient
import com.quizwhiz.userprofileservice.dto.AchievementDto
import com.quizwhiz.userprofileservice.dto.CourseDto
import com.quizwhiz.userprofileservice.dto.ProfileDto
import com.quizwhiz.userprofileservice.dto.RecentQuizAttemptDto
import com.quizwhiz.userprofileservice.service.ProfileService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.ConcurrentModel
import java.util.Date

class ProfileControllerTest {

    private val profileService = mock<ProfileService>()
    private val quizServiceClient = mock<QuizServiceClient>()
    private val controller = ProfileController(profileService, quizServiceClient)

    @Test
    fun `viewProfile should populate model and return profile view if authenticated and authorized`() {
        // Устанавливаем аутентификацию с именем "alice"
        val auth = UsernamePasswordAuthenticationToken("alice", null)
        SecurityContextHolder.getContext().authentication = auth

        // Задаём тестовый профиль
        val profile = ProfileDto(id = 200, username = "alice", email = "alice@example.com", role = "TEACHER")
        whenever(profileService.getProfile("alice")).thenReturn(profile)

        // Задаём тестовые списки. Явно указываем тип для вывода типа T.
        val recentQuizzes: List<RecentQuizAttemptDto> = listOf(
            RecentQuizAttemptDto(
                quizId = "q1",
                quizTitle = "Quiz 1",
                score = 8,
                totalQuestions = 10,
                attemptedAt = Date()
            )
        )
        val recentCourses: List<CourseDto> = listOf(
            CourseDto(id = 1, courseName = "Course 1", description = "Desc 1", teacherId = 200)
        )
        val achievements: List<AchievementDto> = listOf(
            AchievementDto(achievementName = "Champion", description = "Won the quiz", awardedAt = Date())
        )
        whenever(quizServiceClient.getRecentQuizAttempts("alice")).thenReturn(recentQuizzes)
        whenever(quizServiceClient.getCoursesByTeacherId(200)).thenReturn(recentCourses)
        whenever(quizServiceClient.getAchievements("alice")).thenReturn(achievements)

        val model = ConcurrentModel()
        val view = controller.viewProfile("alice", model)
        assertEquals("profile", view)
        assertEquals(profile, model.getAttribute("profile"))
        assertEquals(recentQuizzes, model.getAttribute("recentQuizzes"))
        assertEquals(recentCourses, model.getAttribute("recentCourses"))
        assertEquals(achievements, model.getAttribute("achievements"))
    }

    @Test
    fun `viewProfile should redirect to login if not authenticated or username mismatch`() {
        SecurityContextHolder.clearContext()
        val model = ConcurrentModel()
        val view = controller.viewProfile("alice", model)
        assertEquals("redirect:/login", view)
    }
}
