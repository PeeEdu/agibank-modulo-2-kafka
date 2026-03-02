package com.agibank.kafka_aula2;

import com.agibank.kafka_aula2.dto.TransacaoDTO;
import com.agibank.kafka_aula2.dto.TransacaoFraudeDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

@Service
public class FraudeService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaProducer kafkaProducer;
    private int contador = 0;
    private static final int TTL_MINUTOS = 5;

    public FraudeService(StringRedisTemplate redisTemplate, KafkaProducer kafkaProducer) {
        this.redisTemplate = redisTemplate;
        this.kafkaProducer = kafkaProducer;
    }

    public void processarTransacao(TransacaoDTO transacaoDTO) {
        contador++;

        // a cada 10 transação - DLQ
        if (contador % 10 == 0) {
            kafkaProducer.publishParaDlq(transacaoDTO);
            System.out.printf("Transação enviada para DLQ - cartaoId: %s%n", transacaoDTO.cartaoId());
            return;
        }

        // Verifica se existe algum motivo de fraude
        Optional<String> motivoFraude = verificarFraude(transacaoDTO);

        if (motivoFraude.isPresent()) {
            TransacaoFraudeDTO fraudeDTO = new TransacaoFraudeDTO(
                    transacaoDTO.id(),
                    transacaoDTO.contaId(),
                    transacaoDTO.cartaoId(),
                    transacaoDTO.valor(),
                    transacaoDTO.comerciante(),
                    transacaoDTO.localizacao(),
                    transacaoDTO.tipoTransacao(),
                    transacaoDTO.dataHora(),
                    motivoFraude.get()
            );

            System.out.printf("Fraude detectada (%s) para cartaoId: %s%n",
                    motivoFraude.get(), transacaoDTO.cartaoId());

            kafkaProducer.publishFraude(fraudeDTO);
        } else {
            kafkaProducer.publishValida(transacaoDTO);
        }
    }

    /**
     * Retorna o motivo de fraude se atingir alguma das regras.
     */
    private Optional<String> verificarFraude(TransacaoDTO transacaoDTO) {
        if (isRepeticaoRapida(transacaoDTO.cartaoId())) {
            return Optional.of("Repetição rápida do mesmo cartão");
        }
        if (origemDiferente(transacaoDTO.cartaoId(), transacaoDTO.localizacao())) {
            return Optional.of("Localização diferente da última transação");
        }
        if (valorSuspeito(transacaoDTO.cartaoId(), transacaoDTO.valor())) {
            return Optional.of("Valor muito acima da média");
        }
        if (horarioSuspeito(transacaoDTO)) {
            return Optional.of("Transação em horário suspeito (madrugada)");
        }
        return Optional.empty();
    }

    /**
     * Repetição rápida de transações do mesmo cartão.
     */
    private boolean isRepeticaoRapida(String cartaoId) {
        Boolean exists = redisTemplate.hasKey(cartaoId);
        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.expire(cartaoId, Duration.ofMinutes(TTL_MINUTOS));
            return true;
        } else {
            redisTemplate.opsForValue().set(cartaoId, "1", Duration.ofMinutes(TTL_MINUTOS));
            return false;
        }
    }

    /**
     * Verifica se o mesmo cartão operou em localizações diferentes dentro de um curto prazo.
     */
    private boolean origemDiferente(String cartaoId, String localizacao) {
        String key = "ultimaOrigem:" + cartaoId;
        String ultima = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue().set(key, localizacao, Duration.ofHours(1));
        return ultima != null && !ultima.equals(localizacao);
    }

    /**
     * Verifica se o valor atual é muito superior à média histórica das últimas transações do cartão.
     */
    private boolean valorSuspeito(String cartaoId, BigDecimal valorAtual) {
        String key = "mediaValor:" + cartaoId;
        String anterior = redisTemplate.opsForValue().get(key);
        BigDecimal media = anterior != null ? new BigDecimal(anterior) : valorAtual;
        BigDecimal novaMedia = media.add(valorAtual).divide(BigDecimal.valueOf(2));
        redisTemplate.opsForValue().set(key, novaMedia.toString(), Duration.ofHours(1));
        return valorAtual.compareTo(media.multiply(BigDecimal.valueOf(3))) > 0;
    }

    /**
     * Verifica se a transação ocorreu em horário suspeito (madrugada).
     */
    private boolean horarioSuspeito(TransacaoDTO transacaoDTO) {
        int hora = transacaoDTO.dataHora().getHour();
        return hora < 6 || hora > 23;
    }
}