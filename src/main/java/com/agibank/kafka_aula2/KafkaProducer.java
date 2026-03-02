package com.agibank.kafka_aula2;

import com.agibank.kafka_aula2.dto.TransacaoDTO;
import com.agibank.kafka_aula2.dto.TransacaoFraudeDTO;
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

    public void publishFraude(final TransacaoFraudeDTO transacaoFraude) {
        streamBridge.send("fraudes-out-0", transacaoFraude);
        System.out.printf("Transação fraudulenta enviada: %s - Motivo: %s%n",
                transacaoFraude.cartaoId(), transacaoFraude.motivoFraude());
    }

    public void publishValida(final TransacaoDTO transacao) {
        streamBridge.send("validas-out-0", transacao);
    }
}
