package com.quizwhiz.userprofileservice.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class HomeController {
    @GetMapping("/")
    fun home(): String {
        // Редирект на страницу логина Auth Service
        return "redirect:http://127.0.0.1:8081/login"
    }
}