package com.akbank.wm.middleware;

import java.util.concurrent.Executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MiddlewareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiddlewareApplication.class, args);

    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Set this to an appropriate value based on your requirements
        executor.setMaxPoolSize(20); // Set this to an appropriate value based on your requirements
        executor.setQueueCapacity(500); // Set this to an appropriate value based on your requirements
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }

}
