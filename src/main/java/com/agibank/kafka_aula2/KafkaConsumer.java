package com.agibank.kafka_aula2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
public class KafkaConsumer {

    @Bean
    public Consumer<Message<String>> testConsumer(){
        return message -> {
            System.out.println("Consumindo!");
            System.out.println(message.getPayload());
        };
    }
}
