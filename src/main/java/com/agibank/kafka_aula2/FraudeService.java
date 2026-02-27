package com.agibank.kafka_aula2;

import com.agibank.kafka_aula2.dto.TransacaoDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;

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

        if (contador % 10 == 0) {
            kafkaProducer.publishParaDlq(transacaoDTO);
            System.out.printf("Transação enviada para DLQ - ID %s.%n", transacaoDTO.cartaoId());
            return;
        }
        boolean fraude = isFraude(transacaoDTO);
        if (fraude) {
            System.out.printf("Fraude detectada para - ID %s.%n", transacaoDTO.cartaoId());
            kafkaProducer.publishFraude(transacaoDTO);
        } else {
            kafkaProducer.publishValida(transacaoDTO);
        }
    }

    public boolean isFraude(TransacaoDTO transacaoDTO) {

        boolean suspeitaRepeticao = isRepeticaoRapida(transacaoDTO.cartaoId());

        boolean origemAlterada = origemDiferente(transacaoDTO.cartaoId(), transacaoDTO.localizacao());

        boolean valorAlto = valorSuspeito(transacaoDTO.cartaoId(), transacaoDTO.valor());

        boolean horarioSuspeito = horarioSuspeito(transacaoDTO);

        return suspeitaRepeticao || origemAlterada || valorAlto || horarioSuspeito;
    }

    private boolean isRepeticaoRapida(String chave) {
        Boolean exists = redisTemplate.hasKey(chave);
        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.expire(chave, Duration.ofMinutes(TTL_MINUTOS));
            return true;
        } else {
            redisTemplate.opsForValue().set(chave, "1", Duration.ofMinutes(TTL_MINUTOS));
            return false;
        }
    }

    private boolean origemDiferente(String usuario, String localizacao) {
        String key = "ultimaOrigem:" + usuario;
        String ultima = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue().set(key, localizacao, Duration.ofHours(1));
        return ultima != null && !ultima.equals(localizacao);
    }

    private boolean valorSuspeito(String usuario, BigDecimal valorAtual) {
        String key = "mediaValor:" + usuario;
        String anterior = redisTemplate.opsForValue().get(key);
        BigDecimal media = anterior != null ? new BigDecimal(anterior) : valorAtual;
        BigDecimal novaMedia = media.add(valorAtual).divide(BigDecimal.valueOf(2));
        redisTemplate.opsForValue().set(key, novaMedia.toString(), Duration.ofHours(1));
        return valorAtual.compareTo(media.multiply(BigDecimal.valueOf(3))) > 0;
    }

    private boolean horarioSuspeito(TransacaoDTO transacaoDTO) {
        int hora = transacaoDTO.dataHora().getHour();
        return hora < 6 || hora > 23; // madrugada
    }
}