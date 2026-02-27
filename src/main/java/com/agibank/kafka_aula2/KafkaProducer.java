package com.agibank.kafka_aula2;

import com.agibank.kafka_aula2.dto.MensagemRequest;
import com.agibank.kafka_aula2.dto.TransacaoDTO;
import com.agibank.kafka_aula2.entity.Transacao;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private final StreamBridge streamBridge;

    public KafkaProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publish(final TransacaoDTO transacao) {
        streamBridge.send("test-out-0", transacao);
    }
}
