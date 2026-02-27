package com.agibank.kafka_aula2;

import com.agibank.kafka_aula2.dto.TransacaoDTO;
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

    public void publishParaDlq(final TransacaoDTO transacao) {
        streamBridge.send("dlq-out-0", transacao);
    }

    public void publishFraude(final TransacaoDTO transacao) {
        streamBridge.send("fraudes-out-0", transacao);
    }

    public void publishValida(final TransacaoDTO transacao) {
        streamBridge.send("validas-out-0", transacao);
    }
}
