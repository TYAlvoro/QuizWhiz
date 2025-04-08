package com.quizwhiz.quizservice.controller

import com.quizwhiz.quizservice.model.CourseEntity
import com.quizwhiz.quizservice.repository.CourseRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*

class CourseInternalControllerTest {

    private val courseRepository = mock<CourseRepository>()
    private val controller = CourseInternalController(courseRepository)

    @Test
    fun `getCoursesByTeacher returns list of CourseDto`() {
        val teacherId = 42L
        val courseList = listOf(
            CourseEntity(id=1, courseName="Course1", description="desc1", teacherId=teacherId),
            CourseEntity(id=2, courseName="Course2", description="desc2", teacherId=teacherId)
        )
        whenever(courseRepository.findAllByTeacherId(teacherId)).thenReturn(courseList)

        val result = controller.getCoursesByTeacher(teacherId)
        assertEquals(2, result.size)
        assertEquals("Course1", result[0].courseName)
        assertEquals("Course2", result[1].courseName)
    }
}
