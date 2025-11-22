package org.pecasonline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

import io.sentry.Sentry
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
@EnableCaching
@EnableAsync
class NovoPecasOnlineApplication

fun main(args: Array<String>) {
    runApplication<NovoPecasOnlineApplication>(*args)
}
