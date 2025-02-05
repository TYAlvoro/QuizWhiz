package com.quizwhiz.userprofileservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients(basePackages = ["client"])
class UserprofileServiceApplication

fun main(args: Array<String>) {
	runApplication<UserprofileServiceApplication>(*args)
}
