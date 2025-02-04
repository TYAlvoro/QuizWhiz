package com.quizwhiz.authservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {
    @GetMapping("/")
    fun home(): String {
        println("Я пришел")
        return "Добро пожаловать в Auth Service"
    }
}
