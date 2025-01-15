package org.pecasonline.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.core.task.support.TaskExecutorAdapter
import org.springframework.scheduling.annotation.AsyncConfigurer
import java.util.concurrent.Executors

@Configuration
class AsyncConfig : AsyncConfigurer {

    @Bean
    fun taskExecutor(): AsyncTaskExecutor {
        val threadFactory = Thread.ofVirtual().name("Async-", 0).factory()

        return TaskExecutorAdapter(Executors.newThreadPerTaskExecutor(threadFactory))
    }
}