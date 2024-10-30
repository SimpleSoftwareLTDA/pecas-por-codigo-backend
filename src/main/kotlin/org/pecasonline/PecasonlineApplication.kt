package org.pecasonline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PecasonlineApplication

fun main(args: Array<String>) {
    runApplication<PecasonlineApplication>(*args)
}
