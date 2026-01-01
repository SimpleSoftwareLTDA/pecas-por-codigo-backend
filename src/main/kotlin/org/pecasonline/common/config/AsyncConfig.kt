package org.pecasonline.common.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.AsyncConfigurer
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Configuration
class AsyncConfig : AsyncConfigurer {

    private val logger = KotlinLogging.logger {}

    override fun getAsyncExecutor(): Executor? {
        val threadFactory = Thread.ofVirtual().name("Async-", 0).factory()
        return TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(threadFactory))
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? =
        AsyncUncaughtExceptionHandler { ex, method, params ->
            logger.error(ex) { "Unexpected error occurred in @Async method '${method.name}' with parameters: ${params.joinToString(", ")}" }
        }
}