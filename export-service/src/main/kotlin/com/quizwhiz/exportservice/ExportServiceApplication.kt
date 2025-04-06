package com.quizwhiz.exportservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExportServiceApplication

fun main(args: Array<String>) {
	runApplication<ExportServiceApplication>(*args)
}
