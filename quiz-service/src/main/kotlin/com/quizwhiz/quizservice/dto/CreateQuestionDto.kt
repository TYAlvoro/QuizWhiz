package com.quizwhiz.quizservice.dto

data class CreateQuestionDto(
    var text: String = "",
    // По умолчанию три пустых варианта, пользователь сможет добавить ещё до двух
    var options: MutableList<String> = mutableListOf("", "", ""),
    // Индекс выбранного правильного ответа (от 0 до 4). Если не выбран – null.
    var correctOptionIndex: Int? = null
)
