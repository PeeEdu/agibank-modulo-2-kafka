package com.agibank.kafka_aula2;

import com.agibank.kafka_aula2.dto.TransacaoDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class KafkaConsumer {

    private final FraudeService fraudeService;

    public KafkaConsumer(FraudeService fraudeService) {
        this.fraudeService = fraudeService;
    }

    @Bean
    public Consumer<Message<TransacaoDTO>> testConsumer(){
        return message -> {
            System.out.println("Consumindo!");
            fraudeService.processarTransacao(message.getPayload());
        };
    }
}
