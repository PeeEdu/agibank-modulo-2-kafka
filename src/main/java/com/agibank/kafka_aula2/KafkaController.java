package com.agibank.kafka_aula2;

import com.agibank.kafka_aula2.dto.MensagemRequest;
import com.agibank.kafka_aula2.dto.TransacaoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Kafka", description = "Endpoints para envio de mensagens ao Kafka")
public class KafkaController {

    private final KafkaProducer producer;

    public KafkaController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/send")
    @Operation(summary = "Enviar mensagem ao Kafka")
    public String send(@Valid @RequestBody final TransacaoDTO transacao){
        producer.publish(transacao);
        return "Mensagem Enviada";
    }
}
