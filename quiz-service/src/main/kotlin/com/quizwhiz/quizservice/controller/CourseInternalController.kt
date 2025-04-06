package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.dto.CourseDto
import com.quizwhiz.quizservice.repository.CourseRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/courses")
class CourseInternalController(
    private val courseRepository: CourseRepository
) {
    @GetMapping("/by-teacher/{teacherId}")
    fun getCoursesByTeacher(@PathVariable teacherId: Long): List<CourseDto> {
        val entities = courseRepository.findAllByTeacherId(teacherId)
        return entities.map { entity ->
            CourseDto(
                id = entity.id,
                courseName = entity.courseName,
                description = entity.description,
                teacherId = entity.teacherId
            )
        }
    }
}
