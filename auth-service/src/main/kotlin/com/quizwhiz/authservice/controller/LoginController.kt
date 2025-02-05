package com.quizwhiz.authservice.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {
    @GetMapping("/login")
    fun login(): String {
        return "login"  // Шаблон login.html в src/main/resources/templates
    }
}
