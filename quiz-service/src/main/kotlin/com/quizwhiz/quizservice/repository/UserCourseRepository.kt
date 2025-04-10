package com.quizwhiz.quizservice.repository

import com.quizwhiz.quizservice.model.UserCourseEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserCourseRepository : JpaRepository<UserCourseEntity, Long>
