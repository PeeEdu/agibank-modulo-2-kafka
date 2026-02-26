package com.agibank.kafka_aula2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ChavesExistentesConfig {

    @Bean
    public Map<String, LocalDateTime> chavesExistentes(){
        return new HashMap<>();
    }
}
