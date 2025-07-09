package com.tallerwebi;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class TestConfig {
    @Bean
    @Primary
    public ScheduledExecutorService mockScheduledExecutorService() {
        return Mockito.mock(ScheduledExecutorService.class);
    }

    @Bean
    @Primary
    public SimpMessagingTemplate mockSimpMessagingTemplate() {
        return Mockito.mock(SimpMessagingTemplate.class);
    }
/*
    @Bean
    @Primary
    public PartidaRepository partidaRepository() {
        return Mockito.mock(PartidaRepository.class);
    }

    @Bean
    @Primary
    public RondaService rondaService() {
        return Mockito.mock(RondaService.class);
    }*/
}





