package com.tallerwebi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class ExecutorServiceConfig  {
    @Bean(destroyMethod="shutdown")
    public java.util.concurrent.ScheduledExecutorService scheduledExecutorService() {
        return Executors.newSingleThreadScheduledExecutor();

    }


}