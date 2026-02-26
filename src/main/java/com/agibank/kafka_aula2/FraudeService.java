package com.agibank.kafka_aula2;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class FraudeService {

    private final StringRedisTemplate redisTemplate;
    private static final int TTL_MINUTOS = 1;

    public FraudeService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isFraude(String chave) {
        Boolean exists = redisTemplate.hasKey(chave);
        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.expire(chave, Duration.ofMinutes(TTL_MINUTOS)); // renova TTL
            return true;
        } else {
            redisTemplate.opsForValue().set(chave, "1", Duration.ofMinutes(TTL_MINUTOS));
            return false;
        }
    }
}