package org.pecasonline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableFeignClients
@EnableAsync
class NovoPecasOnlineApplication

fun main(args: Array<String>) {
    runApplication<NovoPecasOnlineApplication>(*args)
}
