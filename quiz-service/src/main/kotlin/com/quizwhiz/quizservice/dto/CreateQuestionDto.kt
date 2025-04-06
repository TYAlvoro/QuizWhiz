package com.quizwhiz.quizservice.dto

data class CreateQuestionDto(
    var text: String = "",
    var options: MutableList<String> = mutableListOf("", "", ""),
    var correctOptionIndex: Int? = null
)
